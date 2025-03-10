package com.lrh.article.infrastructure.doc;

import com.lrh.article.domain.entity.ArticleEntity;
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

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss", format = {})
    private LocalDateTime createTime;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss", format = {})
    private LocalDateTime updateTime;

    @Field(type = FieldType.Nested)
    private List<LabelDO> labels;

    @Field(type = FieldType.Keyword)  // 使用 Keyword 类型存储 userId，不进行分析
    private String userId;  // 新添加的 userId 字段

    @Field(type = FieldType.Keyword)  // 使用 Keyword 类型存储 userName，不进行分析
    private String userName;  // 新添加的 userName 字段

    @Field(type = FieldType.Keyword)
    private Integer isDeleted;


    public ArticleEntity toArticleEntity() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setArticleId(articleId);
        articleEntity.setArticleTitle(articleTitle);
        articleEntity.setArticleContent(articleContent);
        articleEntity.setCreateTime(createTime);
        articleEntity.setUpdateTime(updateTime);
        articleEntity.setUserId(userId);
        articleEntity.setLabelEntityList(labels.stream().map(LabelDO::toLabelEntity).collect(Collectors.toList()));
        return articleEntity;
    }

    public static ArticleDO fromArticleEntity(ArticleEntity articleEntity) {
        ArticleDO articleDO = new ArticleDO();
        articleDO.setArticleId(articleEntity.getArticleId());
        articleDO.setArticleTitle(articleEntity.getArticleTitle());
        articleDO.setArticleContent(articleEntity.getArticleContent());
        articleDO.setCreateTime(articleEntity.getCreateTime());
        articleDO.setUpdateTime(articleEntity.getUpdateTime());
        articleDO.setUserId(articleEntity.getUserId());
        articleDO.setIsDeleted(0);
        List<LabelDO> labelDOList = articleEntity.getLabelEntityList().stream()
                                                 .map(v -> LabelDO.builder()
                                                                  .labelId(v.getLabelId())
                                                                  .labelName(v.getLabelName())
                                                                  .build())
                                                 .collect(Collectors.toList());

        articleDO.setLabels(labelDOList);
        return articleDO;
    }

}