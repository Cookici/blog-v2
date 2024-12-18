package com.lrh.article.domain.entity;

import com.lrh.article.infrastructure.po.LabelPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.entity
 * @ClassName: LabelEntity
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelEntity {
    private String labelId;
    private String labelName;

    public static LabelEntity fromPO(LabelPO labelPO) {
        if(labelPO == null){
            return null;
        }
        return LabelEntity.builder()
                .labelId(labelPO.getLabelId())
                .labelName(labelPO.getLabelName())
                .build();
    }
}
