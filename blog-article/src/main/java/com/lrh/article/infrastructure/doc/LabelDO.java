package com.lrh.article.infrastructure.doc;

import com.lrh.article.domain.entity.LabelEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelDO {
    @Field(type = FieldType.Keyword)
    private String labelId;

    @Field(type = FieldType.Keyword)
    private String labelName;

    public LabelEntity toLabelEntity() {
        LabelEntity labelEntity = new LabelEntity();
        labelEntity.setLabelId(labelId);
        labelEntity.setLabelName(labelName);
        return labelEntity;
    }
}
