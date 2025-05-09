package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.database.mapper.LabelMapper;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.repository
 * @ClassName: LabelRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:26
 */
@Repository
public class LabelRepositoryImpl implements LabelOperateRepository {

    private final LabelMapper labelMapper;

    public LabelRepositoryImpl(LabelMapper labelMapper) {
        this.labelMapper = labelMapper;
    }

    @Override
    public List<LabelPO> getLabelListByIds(List<String> labelIds) {
        LambdaQueryWrapper<LabelPO> queryWrapper = Wrappers.lambdaQuery(LabelPO.class).in(LabelPO::getLabelId, labelIds)
                .eq(LabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<LabelPO> labelPOS = labelMapper.selectList(queryWrapper);
        return labelPOS;
    }

    @Override
    public List<LabelPO> selectLabelsByArticleId(String articleId) {
        List<LabelPO> labelPOList = labelMapper.selectLabelsByArticleId(articleId);
        return labelPOList;
    }

    @Override
    public List<LabelPO> getLabelKinds() {
        return labelMapper.getLabelKinds();
    }

    @Override
    public List<LabelPO> selectLabelsByArticleIdAll(String articleId) {
        return labelMapper.selectLabelsByArticleIdAll(articleId);
    }

    @Override
    public List<LabelPO> getLabelList() {
        return labelMapper.selectList(Wrappers.lambdaQuery(LabelPO.class));
    }

    @Override
    public void update(String labelId, String labelAlias, String labelName, String labelDescription) {
        LambdaUpdateWrapper<LabelPO> updateWrapper = Wrappers.lambdaUpdate(LabelPO.class)
                .eq(LabelPO::getLabelId, labelId)
                .set(LabelPO::getLabelAlias, labelAlias)
                .set(LabelPO::getLabelName, labelName)
                .set(LabelPO::getLabelDescription, labelDescription);
        labelMapper.update(updateWrapper);
    }

    @Override
    public Long countLabel(String keyword) {
        LambdaQueryWrapper<LabelPO> queryWrapper = Wrappers.lambdaQuery(LabelPO.class);
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(LabelPO::getLabelAlias, keyword)
                    .or()
                    .like(LabelPO::getLabelName, keyword)
                    .or()
                    .like(LabelPO::getLabelDescription, keyword)
            );
        }
        return labelMapper.selectCount(queryWrapper);
    }

    @Override
    public List<LabelPO> pageLabel(String keyword, Long limit, Long offset) {
        LambdaQueryWrapper<LabelPO> queryWrapper = Wrappers.lambdaQuery(LabelPO.class);
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(LabelPO::getLabelAlias, keyword)
                    .or()
                    .like(LabelPO::getLabelName, keyword)
                    .or()
                    .like(LabelPO::getLabelDescription, keyword)
            );
        }
        queryWrapper
                .orderByAsc(LabelPO::getCreateTime)
                .last("limit " + offset + ", " + limit);

        return labelMapper.selectList(queryWrapper);
    }

    @Override
    public void delete(String labelId) {
        LambdaUpdateWrapper<LabelPO> updateWrapper = Wrappers.lambdaUpdate(LabelPO.class)
                .eq(LabelPO::getLabelId, labelId)
                .eq(LabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(LabelPO::getIsDeleted, BusinessConstant.IS_DELETED);
        labelMapper.update(updateWrapper);
    }

    @Override
    public List<LabelPO> getDeletedLable() {
        LambdaQueryWrapper<LabelPO> queryWrapper = Wrappers.lambdaQuery(LabelPO.class)
                .eq(LabelPO::getIsDeleted, BusinessConstant.IS_DELETED);
        return labelMapper.selectList(queryWrapper);
    }

    @Override
    public void insert(LabelPO labelPO) {
        labelMapper.insert(labelPO);
    }

    @Override
    public void restore(String labelId) {
        LambdaUpdateWrapper<LabelPO> updateWrapper = Wrappers.lambdaUpdate(LabelPO.class)
                .eq(LabelPO::getLabelId, labelId)
                .eq(LabelPO::getIsDeleted, BusinessConstant.IS_DELETED)
                .set(LabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        labelMapper.update(updateWrapper);
    }
}
