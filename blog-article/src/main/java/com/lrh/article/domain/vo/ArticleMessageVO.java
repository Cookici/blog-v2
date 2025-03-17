package com.lrh.article.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleMessageVO {
    private String articleId;
    private String userName;
    private ArticleStatusEnum status;
}
