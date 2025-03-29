package com.lrh.article.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleMessageVO implements Serializable {
    private String articleId;
    private String userName;
    private ArticleStatusEnum status;
}
