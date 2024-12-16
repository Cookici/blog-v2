package com.lrh.article.infrastructure.database.convertor;

import com.lrh.article.infrastructure.po.ArticleLabelPO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.convertor
 * @ClassName: ArticleLabelConvertor
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/16 11:40
 */

public class ArticleLabelConvertor {
    public static List<ArticleLabelPO> buildArticleLabelPOConvertor(String articleId, List<String> labelIdList) {
        List<ArticleLabelPO> articleLabelPOList = new ArrayList<>();
        labelIdList.forEach(labelId -> {
            ArticleLabelPO articleLabelPO = ArticleLabelPO.builder()
                    .articleId(articleId)
                    .labelId(labelId)
                    .build();
            articleLabelPOList.add(articleLabelPO);
        });
        return articleLabelPOList;
    }
}
