package com.lrh.article.domain.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrh.article.application.cqe.article.*;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.constants.RoleConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.entity.UserArticleDataEntity;
import com.lrh.article.domain.repository.*;
import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.infrastructure.database.convertor.ArticleConvertor;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.event.ArticleUpdateEvent;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.article.util.LockUtil;
import com.lrh.common.context.UserContext;
import com.lrh.common.util.IdUtil;
import com.lrh.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.lrh.article.domain.vo.ArticleStatusEnum.*;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.service
 * @ClassName: ArticleService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:24
 */
@Slf4j
@Service
public class ArticleOperateService {
    private final ArticleOperateRepository articleRepository;
    private final ArticleLabelOperateRepository articleLabelOperateRepository;
    private final LabelOperateRepository labelOperateRepository;
    private final CommentOperateRepository commentOperateRepository;
    private final ArticleCacheRepository articleCacheRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final RedissonClient redissonClient;
    private final ApplicationEventPublisher eventPublisher;


    public ArticleOperateService(ArticleOperateRepository articleRepository,
                                 ArticleLabelOperateRepository articleLabelOperateRepository,
                                 LabelOperateRepository labelOperateRepository,
                                 CommentOperateRepository commentOperateRepository,
                                 ArticleCacheRepository articleCacheRepository,
                                 ArticleLikeRepository articleLikeRepository,
                                 RedissonClient redissonClient,
                                 ApplicationEventPublisher eventPublisher) {

        this.articleRepository = articleRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
        this.labelOperateRepository = labelOperateRepository;
        this.commentOperateRepository = commentOperateRepository;
        this.articleCacheRepository = articleCacheRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.redissonClient = redissonClient;
        this.eventPublisher = eventPublisher;
    }


    public List<ArticleEntity> getArticlesPage(ArticlePageQuery articlePageQuery) {
        List<ArticlePO> articlePOList = articleRepository.getArticlesPage(articlePageQuery,
                articlePageQuery.getOffset(), articlePageQuery.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 为每篇文章设置对应的标签列表
        setLabelListForArticleEntityList(articleEntityList);

        // 返回分页结果
        return articleEntityList;
    }

    private void setLabelListForArticleEntityList(List<ArticleEntity> articleEntityList) {

        if (articleEntityList == null || articleEntityList.isEmpty()) {
            return;
        }

        // 提取文章 ID 列表
        List<String> articleIdList = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());

        // 获取文章与标签 ID 的映射关系
        List<ArticleLabelPO> articleLabelPOList = articleLabelOperateRepository.getArticleLabelListByArticles(articleIdList);
        if(articleLabelPOList == null || articleLabelPOList.isEmpty()){
            return;
        }
        Map<String, List<String>> articleIdToLabelIdsMap = articleLabelPOList.stream().collect(Collectors.groupingBy(
                ArticleLabelPO::getArticleId,
                Collectors.mapping(ArticleLabelPO::getLabelId, Collectors.toList())
        ));

        // 获取标签详细信息
        List<LabelPO> labelPOList = labelOperateRepository.getLabelListByIds(
                articleIdToLabelIdsMap.values().stream()
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList())
        );

        List<LabelEntity> labelEntityList = LabelConvertor.toListLabelEntityConvertor(labelPOList);

        // 构建标签 ID 到标签实体的映射
        Map<String, LabelEntity> labelIdToLabelEntityMap = labelEntityList.stream()
                .collect(Collectors.toMap(LabelEntity::getLabelId, label -> label));

        // 为每篇文章设置对应的标签列表
        articleEntityList.forEach(articleEntity -> {
            List<String> labelIds = articleIdToLabelIdsMap.getOrDefault(articleEntity.getArticleId(), Collections.emptyList());
            List<LabelEntity> labels = labelIds.stream()
                    .map(labelIdToLabelEntityMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            articleEntity.setLabelEntityList(labels);
        });
    }

    /**
     * 计算文章的总数
     *
     * @return 总文章数
     */
    public Long countArticlesPage(ArticlePageQuery articlePageQuery) {
        return articleRepository.countArticlesPage(articlePageQuery);
    }

    public ArticleEntity getArticleById(ArticleQuery articleQuery) {
        ArticlePO articlePO = articleRepository.getArticlesById(articleQuery.getArticleId());
        if (articlePO == null) {
            return null;
        }
        ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
        List<LabelPO> articleIdList = labelOperateRepository.selectLabelsByArticleId(articleQuery.getArticleId());
        if (articleIdList == null) {
            articleEntity.setLabelEntityList(new ArrayList<>());
        } else {
            articleEntity.setLabelEntityList(LabelConvertor.toListLabelEntityConvertor(articleIdList));
        }
        return articleEntity;
    }

    public void syncSaveArticle(ArticleMessageVO article) {
        ArticleEntity articleEntity = getArticleById(new ArticleQuery(article.getArticleId()));
        if (articleEntity == null) {
            return;
        }
        // TODO 内容检测
        articleRepository.updateArticleSatusById(article.getArticleId(), Published.getStatus());
        ArticleDO articleDO = ArticleDO.fromArticleEntity(articleEntity, article.getUserName());
        articleRepository.saveArticleDo(articleDO);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(ArticleDeleteCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
            Integer update = articleRepository.deleteArticleById(command.getArticleId());
            if (update == null || update == 0) {
                return;
            }
            commentOperateRepository.deleteCommentsByArticle(command.getArticleId());
            articleCacheRepository.deleteArticleCache(command.getArticleId());
        });
        eventPublisher.publishEvent(new ArticleUpdateEvent(this, command.getArticleId(), UserContext.getUsername(), Deleted));
    }

    private void validExceptionOperate(String articleId, String userId) {
        String token = UserContext.getToken();
        if (token != null && !token.isEmpty()) {
            DecodedJWT verify = null;
            try {
                verify = JwtUtil.verify(token);
            } catch (Exception e) {
                throw new RuntimeException("非法操作");
            }
            String role = verify.getClaim("role").asString();
            if (role.equals(RoleConstant.ROLE_ADMIN)) {
                return;
            }
        }
        ArticlePO articlePO = articleRepository.getArticlesById(articleId);
        if (articlePO == null || !Objects.equals(articlePO.getUserId(), userId)) {
            throw new RuntimeException("非法操作");
        }
        if (Objects.equals(articlePO.getStatus(), UnderAudit.getStatus())) {
            throw new RuntimeException("审核中博客不允许修改");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArticleById(ArticleUpdateCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleRepository.updateArticleById(command.getArticleId(), command.getArticleTitle(), command.getArticleContent(), UnderAudit.getStatus());
            articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
            if (command.getLabelIdList().isEmpty()) {
                return;
            }
            articleLabelOperateRepository.restoreDeletedArticleLabel(command.getArticleId(), command.getLabelIdList());
            articleLabelOperateRepository.upsertLabelForArticle(command.getArticleId(), command.getLabelIdList());
        });
        eventPublisher.publishEvent(new ArticleUpdateEvent(this, command.getArticleId(), UserContext.getUsername(), UnderAudit));

    }

    @Transactional(rollbackFor = Exception.class)
    public void insertArticle(ArticleInsertCommand command) {
        ArticlePO articlePO = ArticlePO.builder()
                .articleId("article_" + IdUtil.getUuid())
                .articleTitle(command.getArticleTitle())
                .articleContent(command.getArticleContent())
                .userId(command.getUserId())
                .status(UnderAudit.getStatus())
                .build();
        articleRepository.insertArticle(articlePO);
        if (!command.getLabelIdList().isEmpty()) {
            articleLabelOperateRepository.upsertLabelForArticle(articlePO.getArticleId(), command.getLabelIdList());
        }
        eventPublisher.publishEvent(new ArticleUpdateEvent(this, articlePO.getArticleId(), UserContext.getUsername(), UnderAudit));
    }

    @Async("articleAsyncExecutor")
    public void articleViewIncrement(ArticleViewCommand command) {
        articleCacheRepository.incrArticleViewCount(command.getArticleId(), UserContext.getUserId());
    }

    @Async("articleAsyncExecutor")
    public void articleLikeIncrement(ArticleLikeCommand command) {
        Boolean isSuccess = articleCacheRepository.incrArticleLikeCount(command.getArticleId(), UserContext.getUserId());
        if (isSuccess) {
            try {
                articleLikeRepository.incrArticleLikeCount(command.getArticleId(), UserContext.getUserId());
            } catch (Exception e) {
                articleCacheRepository.deleteArticleLike(command.getArticleId(), UserContext.getUserId());
                log.error("文章点赞失败，articleId: {}, userId: {}", command.getArticleId(), UserContext.getUserId(), e);
                throw new RuntimeException("文章点赞失败", e);
            }
        }
    }

    @Async("articleAsyncExecutor")
    public void articleNoLoginViewIncrement(ArticleNoLoginViewCommand command) {
        articleCacheRepository.incrArticleViewCount(command.getArticleId(), command.getIp());
    }

    @Async("articleAsyncExecutor")
    public void articleNoLoginLikeIncrement(ArticleNoLoginLikeCommand command) {
        articleCacheRepository.incrArticleLikeCount(command.getArticleId(), command.getIp());
    }

    public UserArticleDataEntity articlesDataByUserId(String userId) {
        List<ArticlePO> articlePOList = articleRepository.getArticlesByUserId(userId);
        List<String> articleIds = articlePOList.stream()
                .map(ArticlePO::getArticleId)
                .collect(Collectors.toList());
        Long articleCount = (long) articlePOList.size();
        Map<String, Long> articleLikeCountBatch =
                articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Long likeCount = articleLikeCountBatch.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        Map<String, Long> articleViewCountBatch =
                articleCacheRepository.getArticleViewCountBatch(articleIds);
        Long viewCount = articleViewCountBatch.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        return new UserArticleDataEntity(articleCount, likeCount, viewCount);
    }

    public Long countUserArticlesPage(ArticleUserPageQuery query) {
        return articleRepository.countUserArticlesPage(query);
    }

    public List<ArticleEntity> getUserArticlesPage(ArticleUserPageQuery query) {
        List<ArticlePO> articlePOList = articleRepository.getUserArticlesPage(query,
                query.getOffset(), query.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 为每篇文章设置对应的标签列表
        setLabelListForArticleEntityList(articleEntityList);

        // 返回分页结果
        return articleEntityList;
    }

    public Map<String, ArticleEntity> getArticleByIds(List<String> articleIdList) {
        if (articleIdList.isEmpty()) {
            return new HashMap<>();
        }
        List<ArticlePO> articlePOList = articleRepository.getArticleByIds(articleIdList);
        Map<String, ArticleEntity> articleMap = new HashMap<>();
        for (ArticlePO articlePO : articlePOList) {
            ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
            articleMap.put(articleEntity.getArticleId(), articleEntity);
        }
        return articleMap;
    }


    public Long countArticlesEsPage(ArticleListQuery query) {
        return articleRepository.countArticlesByEsQuery(query);
    }

    public List<ArticleEntity> getArticlesEsPage(ArticleListQuery query) {
        List<ArticleDO> articleListByEsQuery = articleRepository.getArticleListByEsQuery(query);
        List<ArticleEntity> articleEntityList = articleListByEsQuery.stream()
                .map(ArticleDO::toArticleEntity)
                .collect(Collectors.toList());
        setLabelListForArticleEntityList(articleEntityList);
        return articleEntityList;
    }

    public void deleteEsArticle(String articleId) {
        articleRepository.deleteEsById(articleId);
    }

    public void deleteArticleLike(String articleId, String userId) {
        Boolean isSuccess = articleCacheRepository.deleteArticleLike(articleId, userId);
        if (isSuccess) {
            try {
                articleLikeRepository.deleteArticleLike(articleId, userId);
            } catch (Exception e) {
                articleCacheRepository.incrArticleLikeCount(articleId, userId);
                throw new RuntimeException(e);
            }
        }
    }

    public Long countLikeArticlesPage(ArticleLikePageQuery query, Set<String> likeArticleIds) {
        if (likeArticleIds == null || likeArticleIds.isEmpty()) {
            return 0L;
        }
        return articleRepository.countLikeArticle(query, likeArticleIds);
    }

    public List<ArticleEntity> getLikeArticlesPage(ArticleLikePageQuery query, Set<String> likeArticleIds) {
        List<ArticleDO> articleDOList = articleRepository.getLikeArticleList(query, likeArticleIds);
        List<ArticleEntity> articleEntityList = articleDOList.stream()
                .map(ArticleDO::toArticleEntity)
                .collect(Collectors.toList());
        setLabelListForArticleEntityList(articleEntityList);
        return articleEntityList;
    }

    public Long countEsUserArticlesEsPage(ArticleEsUserPageQuery query) {
        return articleRepository.countUserArticlesEsPage(query);
    }

    public List<ArticleEntity> getEsUserArticlesEsPage(ArticleEsUserPageQuery query) {
        List<ArticleDO> articleDOList = articleRepository.getUserArticlesEsPage(query);
        if (articleDOList == null) {
            return new ArrayList<>();
        }
        List<ArticleEntity> articleEntityList = articleDOList.stream()
                .map(ArticleDO::toArticleEntity)
                .collect(Collectors.toList());
        setLabelListForArticleEntityList(articleEntityList);
        return articleEntityList;
    }

    public List<ArticleEntity> getHotArticles(ArticleHotQuery query) {
        List<String> articleIds = articleCacheRepository.getUserHotArticleIds(query.getUserId());
        List<ArticleDO> articleDOList = articleRepository.getHotArticles(articleIds);
        if (articleDOList == null || articleDOList.isEmpty()) {
            List<ArticleDO> hotArticlesTop = articleRepository.getHotArticlesTop(10);
            List<ArticleEntity> articleEntityList = hotArticlesTop.stream()
                    .map(ArticleDO::toArticleEntity)
                    .collect(Collectors.toList());
            setLabelListForArticleEntityList(articleEntityList);
            return articleEntityList;
        }
        List<ArticleEntity> articleEntityList = articleDOList.stream()
                .map(ArticleDO::toArticleEntity)
                .collect(Collectors.toList());
        setLabelListForArticleEntityList(articleEntityList);
        return articleEntityList;
    }

    public List<ArticleEntity> getRecommendArticles(String userId) {
        // 1. 获取用户喜欢的文章标签
        List<String> userLikedLabels = articleLabelOperateRepository.getUserLikedLabels(userId);

        // 2. 获取推荐文章
        List<ArticlePO> recommendArticles = articleRepository.getRecommendArticles(userId, userLikedLabels);

        if (recommendArticles == null || recommendArticles.isEmpty()) {
            // 如果没有匹配的推荐文章，返回热门文章
            recommendArticles = articleRepository.getHotArticles(10);
        }

        if (recommendArticles == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(recommendArticles);

        // 为每篇文章设置对应的标签列表
        setLabelListForArticleEntityList(articleEntityList);

        return articleEntityList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(ArticleChangeStatusCommand command) {
        ArticlePO articlePO = articleRepository.getArticlesById(command.getArticleId());
        if (articlePO == null) {
            return;
        }
        if (articlePO.getStatus().equals(UnderAudit.getStatus()) && command.getStatus().equals(Published.getStatus())) {
            articleRepository.updateArticleSatusById(articlePO.getArticleId(), command.getStatus());
            ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
            ArticleDO articleDO = ArticleDO.fromArticleEntity(articleEntity, command.getUserName());
            articleRepository.saveArticleDo(articleDO);
        } else if (articlePO.getStatus().equals(UnderAudit.getStatus()) && command.getStatus().equals(FailedAudit.getStatus())) {
            articleRepository.updateArticleSatusById(articlePO.getArticleId(), command.getStatus());
        } else {
            throw new RuntimeException("规则错误");
        }
    }

    public Long countArticlesPageAll(ArticlePageAllQuery query) {
        return articleRepository.countArticlesPageAll(query);
    }

    public List<ArticleEntity> getArticlesPageAll(ArticlePageAllQuery query) {
        List<ArticlePO> articlePOList = articleRepository.getArticlesPageAll(query,
                query.getOffset(), query.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 为每篇文章设置对应的标签列表
        setIncludeDeleteLabelListForArticleEntityList(articleEntityList);

        // 返回分页结果
        return articleEntityList;
    }

    private void setIncludeDeleteLabelListForArticleEntityList(List<ArticleEntity> articleEntityList) {

        if (articleEntityList == null || articleEntityList.isEmpty()) {
            return;
        }

        // 提取文章 ID 列表
        List<String> articleIdList = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());

        // 获取文章与标签 ID 的映射关系
        List<ArticleLabelPO> articleLabelPOList = articleLabelOperateRepository.getIncludeDeleteArticleLabelListByArticles(articleIdList);
        if(articleLabelPOList == null || articleLabelPOList.isEmpty()) {
            return;
        }
        Map<String, List<String>> articleIdToLabelIdsMap = articleLabelPOList.stream().collect(Collectors.groupingBy(
                ArticleLabelPO::getArticleId,
                Collectors.mapping(ArticleLabelPO::getLabelId, Collectors.toList())
        ));

        // 获取标签详细信息
        List<LabelPO> labelPOList = labelOperateRepository.getLabelListByIds(
                articleIdToLabelIdsMap.values().stream()
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList())
        );

        List<LabelEntity> labelEntityList = LabelConvertor.toListLabelEntityConvertor(labelPOList);

        // 构建标签 ID 到标签实体的映射
        Map<String, LabelEntity> labelIdToLabelEntityMap = labelEntityList.stream()
                .collect(Collectors.toMap(LabelEntity::getLabelId, label -> label));

        // 为每篇文章设置对应的标签列表
        articleEntityList.forEach(articleEntity -> {
            List<String> labelIds = articleIdToLabelIdsMap.getOrDefault(articleEntity.getArticleId(), Collections.emptyList());
            List<LabelEntity> labels = labelIds.stream()
                    .map(labelIdToLabelEntityMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            articleEntity.setLabelEntityList(labels);
        });
    }

    public void restoreDeleted(ArticleRestoreDeletedCommand command) {
        articleRepository.restoreDeleted(command.getArticleId());
        articleRepository.restoreDeletedEs(command.getArticleId());
    }

    public ArticleEntity getDeletedArticleById(ArticleQuery query) {
        ArticlePO articlePO = articleRepository.getDeletedArticlesById(query.getArticleId());
        if (articlePO == null) {
            return null;
        }
        ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
        List<LabelPO> articleIdList = labelOperateRepository.selectLabelsByArticleIdAll(query.getArticleId());
        if (articleIdList == null) {
            articleEntity.setLabelEntityList(new ArrayList<>());
        } else {
            articleEntity.setLabelEntityList(LabelConvertor.toListLabelEntityConvertor(articleIdList));
        }
        return articleEntity;
    }
}
