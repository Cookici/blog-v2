package com.lrh.article.application.cqe.comment;

import com.lrh.article.application.cqe.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageAllQuery extends PageQuery {

    private String keyword;

}
