package com.lrh.article.application.dto.label;

import com.lrh.article.domain.entity.LabelEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.dto.article
 * @ClassName: LabelDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelDTO {
    private String labelId;
    private String labelName;
    private String labelAlias;
    private String labelDescription;

    public static LabelDTO fromEntity(LabelEntity labelEntity) {
        return LabelDTO.builder()
                .labelId(labelEntity.getLabelId())
                .labelName(labelEntity.getLabelName())
                .labelAlias(labelEntity.getLabelAlias())
                .labelDescription(labelEntity.getLabelDescription())
                .build();
    }


    public static List<LabelDTO> fromEntityList(List<LabelEntity> labelEntityList) {
        return labelEntityList.stream().map(LabelDTO::fromEntity).collect(Collectors.toList());
    }
}
