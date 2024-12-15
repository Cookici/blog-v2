package com.lrh.article.infrastructure.database.convertor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.infrastructure.po.ArticlePO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.convertor
 * @ClassName: ArticleConvertor
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:47
 */

public class ArticleConvertor {

    public static Page<ArticleEntity> toPageArticleEntityConvertor(Page<ArticlePO> articlePOPage) {
        List<ArticlePO> articles = articlePOPage.getRecords();
        List<ArticleEntity> articleEntityList = new ArrayList<>();
        articles.forEach(article -> {
            articleEntityList.add(ArticleEntity.fromPO(article));
        });

        Page<ArticleEntity> articleEntityPage = new Page<>(articlePOPage.getCurrent(),
                articlePOPage.getSize(), articlePOPage.getTotal());
        articleEntityPage.setRecords(articleEntityList);
        return articleEntityPage;
    }

}
