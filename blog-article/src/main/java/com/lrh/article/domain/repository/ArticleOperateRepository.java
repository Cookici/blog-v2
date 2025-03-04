package com.lrh.article.domain.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lrh.article.application.cqe.article.ArticleListQuery;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.application.cqe.article.ArticleUserPageQuery;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.springframework.data.domain.Page;

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

    Integer updateArticleSatusById(String articleId, String status);

    void updateArticleById(String articleId, String articleTitle, String articleContent, String status);

    void insertArticle(ArticlePO articlePO);

    List<ArticlePO> countArticlesByUserId(String userId);

    Long countUserArticlesPage(ArticleUserPageQuery query);

    List<ArticlePO> getUserArticlesPage(ArticleUserPageQuery query, Long offset, Long limit);

    List<ArticlePO> getArticleByIds(List<String> articleIdList);


    Page<ArticleDO> findArticleListByQuery(ArticleListQuery query);

    void deleteEsById(String articleId);

    void saveArticleDo(ArticleDO articleDO);
}
