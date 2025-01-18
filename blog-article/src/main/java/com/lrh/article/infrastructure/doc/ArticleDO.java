package com.lrh.article.infrastructure.doc;

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

    @Field(type = FieldType.Date)  // 使用 Date 类型存储日期
    private LocalDateTime createTime;

    @Field(type = FieldType.Date)  // 使用 Date 类型存储日期
    private LocalDateTime updateTime;

    @Field(type = FieldType.Integer)  // 使用 Integer 类型存储 isDeleted
    private Integer isDeleted;

    @Field(type = FieldType.Keyword)  // 使用 Keyword 类型存储标签，不进行分析
    private List<String> labels;
}
