package com.lrh.article.infrastructure.doc;

import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "article_index")
public class ArticleDO {

    @Id  // 主键 ID，Elasticsearch 会自动生成
    private String articleId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")  // 设置分词器为 ik_max_word
    private String articleTitle;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")  // 设置分词器为 ik_max_word
    private String articleContent;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;

    @Field(type = FieldType.Integer)  // 使用 Integer 类型存储 isDeleted
    private Integer isDeleted;

    @Field(type = FieldType.Keyword)  // 使用 Keyword 类型存储标签，不进行分析
    private List<LabelDO> labels;

    @Field(type = FieldType.Keyword)  // 使用 Keyword 类型存储 userId，不进行分析
    private String userId;  // 新添加的 userId 字段

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LabelDO {
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

    public ArticleEntity toArticleEntity() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setArticleId(articleId);
        articleEntity.setArticleTitle(articleTitle);
        articleEntity.setArticleContent(articleContent);
        articleEntity.setCreateTime(createTime);
        articleEntity.setUpdateTime(updateTime);
        articleEntity.setUserId(userId);
        articleEntity.setLabelEntityList(
                labels.stream()
                      .map(LabelDO::toLabelEntity)
                      .collect(Collectors.toList())
        );

        return articleEntity;
    }

}
