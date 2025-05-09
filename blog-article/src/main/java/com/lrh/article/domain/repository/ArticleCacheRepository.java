package com.lrh.article.domain.repository;

import com.lrh.article.application.dto.article.ArticleDTO;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleCacheRepository
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/15 17:44
 */

public interface ArticleCacheRepository {

    void incrArticleViewCount(String articleId,String ukId);

    Boolean incrArticleLikeCount(String articleId, String ukId);

    Long getArticleLikeCount(String articleId);

    Long getArticleViewCount(String articleId);

    Map<String,Long> getArticleLikeCountBatch(List<String> articleId);

    Map<String,Long> getArticleViewCountBatch(List<String> articleId);

    void deleteArticleCache(String articleId);

    Boolean deleteArticleLike(String articleId,String ukId);

    List<String> getUserHotArticleIds(String userId);

    void saveUserHotArticleIds(String userId, List<String> articleIds);

    List<ArticleDTO> getUserRecommendArticles(String userId);

    void saveUserRecommendCache(String userId, List<ArticleDTO> articleDTOList);

    void restoreArticleLikeAndView(String articleId,  List<String> userIdList, Long viewCount);
}
