package com.lrh.article.domain.repository;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticlePO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleRepository
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:54
 */

public interface ArticleOperateRepository {
    List<ArticlePO> getArticlesPage(ArticlePageQuery query, Long offset, Long limit);

    Long countArticlesPage(ArticlePageQuery query);

    ArticlePO getArticlesById(String articleId);

    Integer deleteArticleById(String articleId);

    Integer updateArticleSatusById(String articleId, String status);

    void updateArticleById(String articleId, String articleTitle, String articleContent, String status);

    void insertArticle(ArticlePO articlePO);

    List<ArticlePO> getArticlesByUserId(String userId);

    Long countUserArticlesPage(ArticleUserPageQuery query);

    List<ArticlePO> getUserArticlesPage(ArticleUserPageQuery query, Long offset, Long limit);

    List<ArticlePO> getArticleByIds(List<String> articleIdList);


    List<ArticleDO> getArticleListByEsQuery(ArticleListQuery query);

    Long countArticlesByEsQuery(ArticleListQuery query);

    void deleteEsById(String articleId);

    void saveArticleDo(ArticleDO articleDO);

    Long countLikeArticle(ArticleLikePageQuery query, Set<String> likeIds);

    List<ArticleDO> getLikeArticleList(ArticleLikePageQuery query, Set<String> likeArticleIds);


    /**
     * 获取所有文章ID
     * @return 所有文章ID列表
     */
    List<String> getAllArticleIds();

    /**
     * 批量更新文章点赞和浏览量数据
     * @param updateBatch 更新数据批次
     */
    void batchUpdateArticleMetrics(List<Map<String, Object>> updateBatch);

    /**
     * 更新ES中文章的点赞和浏览量数据
     * @param articleId 文章ID
     * @param likeCount 点赞数
     * @param viewCount 浏览量
     */
    void updateArticleEsMetrics(String articleId, Long likeCount, Long viewCount);

    Long countUserArticlesEsPage(ArticleEsUserPageQuery query);

    List<ArticleDO> getUserArticlesEsPage(ArticleEsUserPageQuery query);

    List<ArticleDO> getHotArticles(List<String> articleIds);

    List<ArticleDO> getHotArticlesTop(Integer top);

    /**
     * 获取推荐文章列表
     * @param userId 用户ID
     * @param labelNames 用户喜欢的标签列表
     * @return 推荐文章列表
     */
    List<ArticlePO> getRecommendArticles(String userId, List<String> labelNames);

    /**
     * 获取热门文章列表
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<ArticlePO> getHotArticles(int limit);

    /**
     * 获取需要对账的文章总数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 文章总数
     */
    Long countArticlesForReconciliation(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取需要对账的文章ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 文章ID列表
     */
    List<String> getArticleIdsForReconciliation(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    
    /**
     * 从ES获取指定ID的文章列表
     * @param articleIds 文章ID列表
     * @return ES中的文章列表
     */
    List<ArticleDO> getArticleEsByIds(List<String> articleIds);

    int batchSaveArticleDo(List<ArticleDO> articlesToAdd);

    int batchUpdateArticleDo(List<ArticleDO> needUpdateEsArticles);
}
