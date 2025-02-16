package com.lrh.article.domain.repository;

import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleLikeRepository
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 20:41
 */

public interface ArticleLikeRepository {

    void incrArticleLikeCount(String articleId, String userId);

    void deleteArticleLike(String articleId, String userId);

    Set<String> getLikedArticleIdsByUserId(String userId);

    Map<String, Set<String>> getUserLikedArticlesMap();

}
