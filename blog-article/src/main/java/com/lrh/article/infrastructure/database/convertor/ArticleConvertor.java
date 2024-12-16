package com.lrh.article.infrastructure.database.convertor;

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

    public static List<ArticleEntity> toArticleEntityListConvertor(List<ArticlePO> articlePOList) {
        List<ArticleEntity> articleEntityList = new ArrayList<>();
        articlePOList.forEach(articlePO -> {
            articleEntityList.add(ArticleEntity.fromPO(articlePO));
        });
        return articleEntityList;
    }
}
