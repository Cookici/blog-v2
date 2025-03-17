package com.lrh.article.application.task;

import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.task
 * @ClassName: ArticleMetricsTask
 * @Description: 文章指标数据同步定时任务
 */
@Slf4j
@Component
public class ArticleMetricsTask {

    private final ArticleOperateRepository articleRepository;
    private final ArticleCacheRepository articleCacheRepository;
    private final RedissonClient redissonClient;

    public ArticleMetricsTask(ArticleOperateRepository articleRepository,
                              ArticleCacheRepository articleCacheRepository,
                              RedissonClient redissonClient) {
        this.articleRepository = articleRepository;
        this.articleCacheRepository = articleCacheRepository;
        this.redissonClient = redissonClient;
    }

    /**
     * 定时同步文章点赞和浏览量数据到ES和数据库
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 600000)
    public void syncArticleMetricsData() {
        log.info("开始同步文章点赞和浏览量数据");
        try {
            LockUtil lockUtil = new LockUtil(redissonClient);
            lockUtil.tryLock(RedisConstant.ARTICLE_METRICS_SYNC_LOCK, () -> {
                // 获取所有文章ID
                List<String> allArticleIds = articleRepository.getAllArticleIds();
                if (allArticleIds.isEmpty()) {
                    return;
                }
                
                // 批量获取缓存中的点赞和浏览量数据
                Map<String, Long> articleLikeCountMap = articleCacheRepository.getArticleLikeCountBatch(allArticleIds);
                Map<String, Long> articleViewCountMap = articleCacheRepository.getArticleViewCountBatch(allArticleIds);
                
                // 批量更新数据库中的点赞和浏览量数据
                List<Map<String, Object>> updateBatch = new ArrayList<>();
                for (String articleId : allArticleIds) {
                    Long likeCount = articleLikeCountMap.getOrDefault(articleId, 0L);
                    Long viewCount = articleViewCountMap.getOrDefault(articleId, 0L);
                    
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("articleId", articleId);
                    updateData.put("likeCount", likeCount);
                    updateData.put("viewCount", viewCount);
                    updateBatch.add(updateData);
                }
                
                if (!updateBatch.isEmpty()) {
                    // 更新数据库
                    articleRepository.batchUpdateArticleMetrics(updateBatch);
                    
                    // 并行更新ES
                    updateBatch.parallelStream().forEach(data -> {
                        try {
                            String articleId = (String) data.get("articleId");
                            Long likeCount = (Long) data.get("likeCount");
                            Long viewCount = (Long) data.get("viewCount");
                            articleRepository.updateArticleEsMetrics(articleId, likeCount, viewCount);
                        } catch (Exception e) {
                            log.error("更新文章ES指标数据失败，articleId: {}, error: {}", 
                                    data.get("articleId"), e.getMessage());
                        }
                    });
                }
                
                log.info("文章点赞和浏览量数据同步完成，共同步{}篇文章", updateBatch.size());
            });
        } catch (Exception e) {
            log.error("同步文章点赞和浏览量数据失败", e);
        }
    }
}