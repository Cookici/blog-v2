package com.lrh.article.application.task;

import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.domain.vo.ArticleStatusEnum;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.article.util.LockUtil;
import com.lrh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文章数据库与ES数据一致性对账任务
 */
@Slf4j
@Component
public class ArticleDataConsistencyTask {

    private final ArticleOperateRepository articleRepository;
    private final RedissonClient redissonClient;
    private final UserClient userClient;

    /**
     * 每批处理的文章数量
     */
    private static final int BATCH_SIZE = 100;

    public ArticleDataConsistencyTask(ArticleOperateRepository articleRepository,
                                      RedissonClient redissonClient, UserClient userClient) {
        this.articleRepository = articleRepository;
        this.redissonClient = redissonClient;
        this.userClient = userClient;
    }

    /**
     * 每天凌晨3点执行增量对账
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyIncrementalReconciliation() {
        log.info("开始执行文章数据库与ES每日增量对账");
        try {
            LockUtil lockUtil = new LockUtil(redissonClient);
            lockUtil.tryLock(RedisConstant.ARTICLE_RECONCILIATION_LOCK, () -> {
                // 获取昨天的开始和结束时间
                LocalDateTime yesterdayStart = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
                LocalDateTime yesterdayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

                // 执行增量对账
                reconcileArticles(yesterdayStart, yesterdayEnd);
            });
        } catch (Exception e) {
            log.error("文章数据库与ES每日增量对账失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每周凌晨4点执行全量对账
     */
    @Scheduled(cron = "0 0 4 ? * SUN")
    public void weeklyFullReconciliation() {
        log.info("开始执行文章数据库与ES每周全量对账");
        try {
            LockUtil lockUtil = new LockUtil(redissonClient);
            lockUtil.tryLock(RedisConstant.ARTICLE_FULL_RECONCILIATION_LOCK, () -> {
                // 执行全量对账，不传时间范围
                reconcileArticles(null, null);
            });
        } catch (Exception e) {
            log.error("文章数据库与ES每周全量对账失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 执行文章对账
     *
     * @param startTime 开始时间，如果为null则不限制开始时间
     * @param endTime   结束时间，如果为null则不限制结束时间
     */
    private void reconcileArticles(LocalDateTime startTime, LocalDateTime endTime) {
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger fixedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        try {
            // 先查询符合条件的文章总数
            Long articleTotalCount = articleRepository.countArticlesForReconciliation(startTime, endTime);
            if (articleTotalCount == null || articleTotalCount == 0) {
                log.info("没有需要对账的文章");
                return;
            }

            log.info("开始对账，符合条件的文章总数: {}", articleTotalCount);

            // 计算总页数
            int totalPages = (int) Math.ceil(articleTotalCount.doubleValue() / BATCH_SIZE);

            // 分页处理
            for (int page = 1; page <= totalPages; page++) {
                // 获取一批文章ID
                List<String> articleBatch = articleRepository.getArticleIdsForReconciliation(startTime, endTime, page, BATCH_SIZE);
                if (articleBatch == null || articleBatch.isEmpty()) {
                    break;
                }

                totalCount.addAndGet(articleBatch.size());
                log.info("正在对账第{}批文章，共{}篇，总进度: {}/{}",
                        page, articleBatch.size(), Math.min(totalCount.get(), articleTotalCount), articleTotalCount);

                // 从数据库获取文章详情
                List<ArticlePO> dbArticles = articleRepository.getArticleByIds(articleBatch);
                Map<String, ArticlePO> dbArticleMap = new HashMap<>(dbArticles.size() * 4 / 3 + 1);
                for (ArticlePO article : dbArticles) {
                    dbArticleMap.put(article.getArticleId(), article);
                }

                // 从ES获取文章详情
                List<ArticleDO> esArticles = articleRepository.getArticleEsByIds(articleBatch);
                Map<String, ArticleDO> esArticleMap = new HashMap<>(esArticles.size() * 4 / 3 + 1);
                for (ArticleDO article : esArticles) {
                    esArticleMap.put(article.getArticleId(), article);
                }

                // 预先获取所有需要的用户名
                Set<String> userIds = new HashSet<>();
                for (ArticlePO article : dbArticles) {
                    if (article.getUserId() != null) {
                        userIds.add(article.getUserId());
                    }
                }
                Map<String, String> userNameMap = batchGetUserNames(userIds);

                // 使用并行流对比并修复不一致的数据
                List<String> missingInEsIds = Collections.synchronizedList(new ArrayList<>());
                List<ArticleDO> needUpdateEsArticles = Collections.synchronizedList(new ArrayList<>());

                // 使用并行流处理文章比对
                articleBatch.parallelStream().forEach(articleId -> {
                    ArticlePO dbArticle = dbArticleMap.get(articleId);
                    ArticleDO esArticle = esArticleMap.get(articleId);

                    // 数据库发布了有但ES没有的文章
                    if (dbArticle != null && esArticle == null && Objects.equals(dbArticle.getStatus(), ArticleStatusEnum.Published.getStatus())) {
                        missingInEsIds.add(articleId);
                        return;
                    }

                    // 两边都有，但数据不一致
                    if (dbArticle != null && esArticle != null) {
                        boolean needUpdate = false;

                        // 创建一个新的ES文章对象，避免并发修改问题
                        ArticleDO updatedEsArticle = new ArticleDO();
                        BeanUtils.copyProperties(esArticle, updatedEsArticle);

                        // 检查标题
                        if (!Objects.equals(dbArticle.getArticleTitle(), esArticle.getArticleTitle())) {
                            updatedEsArticle.setArticleTitle(dbArticle.getArticleTitle());
                            needUpdate = true;
                        }

                        // 检查内容
                        if (!Objects.equals(dbArticle.getArticleContent(), esArticle.getArticleContent())) {
                            updatedEsArticle.setArticleContent(dbArticle.getArticleContent());
                            needUpdate = true;
                        }

                        // 检查用户ID
                        if (!Objects.equals(dbArticle.getUserId(), esArticle.getUserId())) {
                            updatedEsArticle.setUserId(dbArticle.getUserId());
                            // 同时更新用户名
                            String userName = userNameMap.getOrDefault(dbArticle.getUserId(), "未知用户[fail]");
                            updatedEsArticle.setUserName(userName);
                            needUpdate = true;
                        } else {
                            // 即使用户ID相同，也需要检查用户名是否一致
                            String expectedUserName = userNameMap.getOrDefault(dbArticle.getUserId(), "未知用户[fail]");
                            if (!Objects.equals(expectedUserName, esArticle.getUserName()) && !Objects.equals(expectedUserName, "未知用户[fail]")) {
                                updatedEsArticle.setUserName(expectedUserName);
                                needUpdate = true;
                                log.info("文章ID: {}，用户ID相同但用户名不一致，更新用户名: {} -> {}",
                                        articleId, esArticle.getUserName(), expectedUserName);
                            }
                        }

                        // 检查创建时间
                        if (!Objects.equals(dbArticle.getCreateTime(), esArticle.getCreateTime())) {
                            updatedEsArticle.setCreateTime(dbArticle.getCreateTime());
                            needUpdate = true;
                        }

                        // 检查更新时间
                        if (!Objects.equals(dbArticle.getUpdateTime(), esArticle.getUpdateTime())) {
                            updatedEsArticle.setUpdateTime(dbArticle.getUpdateTime());
                            needUpdate = true;
                        }

                        // 检查删除状态
                        if (!Objects.equals(dbArticle.getIsDeleted(), esArticle.getIsDeleted())) {
                            updatedEsArticle.setIsDeleted(dbArticle.getIsDeleted());
                            needUpdate = true;
                        }

                        if (needUpdate) {
                            needUpdateEsArticles.add(updatedEsArticle);
                        }
                    }
                });

                // 批量处理修复操作，减少IO次数
                processBatchFixes(missingInEsIds, needUpdateEsArticles, dbArticleMap, userNameMap, fixedCount, errorCount);
            }

            log.info("文章数据库与ES对账完成，共检查{}篇文章，修复{}篇不一致数据，失败{}篇",
                    totalCount.get(), fixedCount.get(), errorCount.get());

        } catch (Exception e) {
            log.error("执行文章对账过程中发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 批量处理修复操作
     */
    private void processBatchFixes(List<String> missingInEsIds, List<ArticleDO> needUpdateEsArticles,
                                   Map<String, ArticlePO> dbArticleMap, Map<String, String> userNameMap,
                                   AtomicInteger fixedCount, AtomicInteger errorCount) {
        // 修复缺失的文章
        if (!missingInEsIds.isEmpty()) {
            List<ArticleDO> articlesToAdd = new ArrayList<>(missingInEsIds.size());

            for (String articleId : missingInEsIds) {
                try {
                    ArticlePO dbArticle = dbArticleMap.get(articleId);
                    if (dbArticle != null) {
                        ArticleDO articleDO = new ArticleDO();
                        // 设置文章基本信息
                        articleDO.setArticleId(dbArticle.getArticleId());
                        articleDO.setArticleTitle(dbArticle.getArticleTitle());
                        articleDO.setArticleContent(dbArticle.getArticleContent());
                        articleDO.setUserId(dbArticle.getUserId());

                        // 从预先获取的用户名映射中获取用户名
                        String userName = userNameMap.getOrDefault(dbArticle.getUserId(), "未知用户");
                        articleDO.setUserName(userName);

                        articleDO.setLikeCount(dbArticle.getLikeCount());
                        articleDO.setViewCount(dbArticle.getViewCount());
                        articleDO.setCreateTime(dbArticle.getCreateTime());
                        articleDO.setUpdateTime(dbArticle.getUpdateTime());
                        articleDO.setIsDeleted(dbArticle.getIsDeleted());

                        articlesToAdd.add(articleDO);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.error("准备ES缺失文章数据失败，articleId: {}, error: {}", articleId, e.getMessage());
                }
            }

            // 批量保存文章到ES
            try {
                if (!articlesToAdd.isEmpty()) {
                    int savedCount = articleRepository.batchSaveArticleDo(articlesToAdd);
                    fixedCount.addAndGet(savedCount);
                    log.info("批量添加ES缺失文章{}篇", savedCount);
                }
            } catch (Exception e) {
                errorCount.addAndGet(articlesToAdd.size());
                log.error("批量添加ES缺失文章失败: {}", e.getMessage(), e);
            }
        }

        // 批量更新需要修复的文章
        if (!needUpdateEsArticles.isEmpty()) {
            try {
                int updatedCount = articleRepository.batchUpdateArticleDo(needUpdateEsArticles);
                fixedCount.addAndGet(updatedCount);
                log.info("批量更新ES文章{}篇", updatedCount);
            } catch (Exception e) {
                errorCount.addAndGet(needUpdateEsArticles.size());
                log.error("批量更新ES文章失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 批量获取用户名
     *
     * @param userIds 用户ID集合
     * @return 用户ID到用户名的映射
     */
    private Map<String, String> batchGetUserNames(Set<String> userIds) {
        Map<String, String> userNameMap = new HashMap<>(userIds.size() * 4 / 3 + 1);
        if (userIds.isEmpty()) {
            return userNameMap;
        }

        try {
            // 检查当前是否有请求上下文
            boolean hasRequestContext = RequestContextHolder.getRequestAttributes() != null;
            
            // 如果没有请求上下文，创建一个模拟的请求上下文
            ServletRequestAttributes originalAttributes = null;
            if (!hasRequestContext) {
                MockHttpServletRequest mockRequest = new MockHttpServletRequest();
                ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
                originalAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                RequestContextHolder.setRequestAttributes(attributes);
                log.info("为Feign客户端创建模拟请求上下文");
            }
            
            try {
                List<String> userIdList = new ArrayList<>(userIds);
                Result<Map<String, UserVO>> result = userClient.getByIds(userIdList);

                if (result != null && result.getCode() == HttpStatus.OK.value() && result.getData() != null) {
                    Map<String, UserVO> userMap = result.getData();
                    for (Map.Entry<String, UserVO> entry : userMap.entrySet()) {
                        if (entry.getValue() != null) {
                            userNameMap.put(entry.getKey(), entry.getValue().getUserName());
                        }
                    }
                }
            } finally {
                // 如果我们创建了模拟上下文，恢复原始上下文
                if (!hasRequestContext) {
                    RequestContextHolder.setRequestAttributes(originalAttributes);
                    log.info("已恢复原始请求上下文");
                }
            }
        } catch (Exception e) {
            log.error("批量获取用户名失败: {}", e.getMessage(), e);
        }

        return userNameMap;
    }
}