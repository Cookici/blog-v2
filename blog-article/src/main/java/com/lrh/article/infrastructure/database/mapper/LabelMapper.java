package com.lrh.article.infrastructure.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.infrastructure.po.LabelPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.mapper
 * @ClassName: LabelMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:25
 */
@Mapper
public interface LabelMapper extends BaseMapper<LabelPO> {

    List<LabelPO> selectLabelsByArticleId(String articleId);

    List<LabelPO> getLabelKinds();

    List<LabelPO> selectLabelsByArticleIdAll(String articleId);
}
