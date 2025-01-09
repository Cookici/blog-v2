package com.lrh.article.domain.repository;

import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.infrastructure.po.ArticlePO;

import java.util.List;

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

    void updateArticleById(String articleId, String articleTitle, String articleContent);

    void insertArticle(ArticlePO articlePO);
}
