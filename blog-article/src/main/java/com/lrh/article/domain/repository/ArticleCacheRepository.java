package com.lrh.article.domain.repository;

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

    void incrArticleLikeCount(String articleId, String ukId);
}
