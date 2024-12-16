package com.lrh.article.domain.repository;

import com.lrh.article.infrastructure.po.LabelPO;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: LabelOperateRepository
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:26
 */

public interface LabelOperateRepository {
    List<LabelPO> getLabelListByIds(List<String> labelIds);

    List<LabelPO> selectLabelsByArticleId(String articleId);
}
