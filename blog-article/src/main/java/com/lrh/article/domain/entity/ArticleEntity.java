package com.lrh.article.domain.entity;

import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticlePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.entity
 * @ClassName: ArticleEntity
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEntity {
    private String articleId;
    private String userId;
    private String status;
    private String articleTitle;
    private String articleContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<LabelEntity> labelEntityList;

    public static ArticleEntity fromPO(ArticlePO articlePO) {
        if(articlePO == null){
            return null;
        }
        return ArticleEntity.builder()
                .articleId(articlePO.getArticleId())
                .userId(articlePO.getUserId())
                .articleTitle(articlePO.getArticleTitle())
                .articleContent(articlePO.getArticleContent())
                .createTime(articlePO.getCreateTime())
                .updateTime(articlePO.getUpdateTime())
                .status(articlePO.getStatus())
                .build();
    }
}
