package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
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
    public List<LabelEntity> getLabelListByIds(List<String> labelIds) {
        LambdaQueryWrapper<LabelPO> queryWrapper = Wrappers.lambdaQuery(LabelPO.class).in(LabelPO::getLabelId, labelIds)
                .eq(LabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<LabelPO> labelPOS = labelMapper.selectList(queryWrapper);
        return LabelConvertor.toListLabelEntityConvertor(labelPOS);
    }
}
