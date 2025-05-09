package com.lrh.article.application.dto.label;

import com.lrh.article.domain.entity.LabelEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelAdminDTO {
    private String labelId;
    private String labelName;
    private String labelAlias;
    private String labelDescription;
    private Integer isDeleted;

    public static LabelAdminDTO fromEntity(LabelEntity labelEntity) {
        return LabelAdminDTO.builder()
                .labelId(labelEntity.getLabelId())
                .labelName(labelEntity.getLabelName())
                .labelAlias(labelEntity.getLabelAlias())
                .labelDescription(labelEntity.getLabelDescription())
                .isDeleted(labelEntity.getIsDeleted())
                .build();
    }


    public static List<LabelAdminDTO> fromEntityList(List<LabelEntity> labelEntityList) {
        if (labelEntityList == null) {
            return new ArrayList<>();
        }
        return labelEntityList.stream().map(LabelAdminDTO::fromEntity).collect(Collectors.toList());
    }
}
