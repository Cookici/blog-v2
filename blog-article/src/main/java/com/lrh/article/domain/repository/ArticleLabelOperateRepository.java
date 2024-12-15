package com.lrh.article.domain.repository;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: ArticleLabelOperateRepository
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:45
 */

public interface ArticleLabelOperateRepository {

    Map<String, List<String>> getArticleIdMapLableIdList(List<String> articleIdList);
}
