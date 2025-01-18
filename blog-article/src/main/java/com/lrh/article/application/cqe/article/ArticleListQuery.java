package com.lrh.article.application.cqe.article;


import com.lrh.article.application.cqe.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListQuery extends PageQuery {
    private String element;
}
