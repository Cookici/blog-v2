package com.lrh.article.domain.entity;

import com.lrh.article.infrastructure.po.ArticlePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String articleTitle;
    private String articleContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static ArticleEntity fromPO(ArticlePO articlePO) {
        return new ArticleEntity(
                articlePO.getArticleId(),
                articlePO.getUserId(),
                articlePO.getArticleTitle(),
                articlePO.getArticleContent(),
                articlePO.getCreateTime(),
                articlePO.getUpdateTime()
        );
    }
}
