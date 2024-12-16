package com.lrh.article.domain.repository;

import com.lrh.article.infrastructure.po.ArticleLabelPO;

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

    List<ArticleLabelPO> getArticleIdMapLableIdList(List<String> articleIdList);
}
