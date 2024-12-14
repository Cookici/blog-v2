package com.lrh.article.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.infrastructure.po.ArticlePO;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleRepository
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:54
 */

public interface ArticleOperateRepository {
    Page<ArticlePO> getArticlesPage(ArticlePageQuery query);

    Long countArticlesPage(ArticlePageQuery query);
}
