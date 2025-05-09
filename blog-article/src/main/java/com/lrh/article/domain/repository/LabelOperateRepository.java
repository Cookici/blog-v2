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

    List<LabelPO> getLabelKinds();

    List<LabelPO> selectLabelsByArticleIdAll(String articleId);

    List<LabelPO> getLabelList();

    void update(String labelId ,String labelAlias, String labelName, String labelDescription);

    Long countLabel(String keyword);

    List<LabelPO> pageLabel(String keyword, Long limit, Long offset);

    void delete(String labelId);

    List<LabelPO> getDeletedLable();

    void insert(LabelPO labelPO);

    void restore(String labelId);

    Long count();
}
