package com.lrh.article.infrastructure.database.convertor;

import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.infrastructure.po.LabelPO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.convertor
 * @ClassName: LabelConvertor
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:51
 */

public class LabelConvertor {
    public static List<LabelEntity> toListLabelEntityConvertor(List<LabelPO> labelPOList) {
        List<LabelEntity> labelEntityList = new ArrayList<>();
        labelPOList.forEach(labelPO -> {
            labelEntityList.add(LabelEntity.fromPO(labelPO));
        });
        return labelEntityList;
    }
}
