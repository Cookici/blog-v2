package com.lrh.article.domain.repository;

import com.lrh.article.application.cqe.article.ArticleListQuery;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleLabelOperateRepository
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:45
 */

public interface ArticleLabelOperateRepository {

    void upsertLabelForArticle(String articleId, List<String> labelIdList);

    void deleteLabelForArticle(String articleId);

    void restoreDeletedArticleLabel(String articleId, List<String> labelIdList);

    List<ArticleLabelPO> getArticleLabelListByArticles(List<String> articleIdList);

    // 通过ES模糊查询(标签，正文，标题)博客
    Page<ArticleDO> findArticleListByQuery(ArticleListQuery query);
}
