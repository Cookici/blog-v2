package com.lrh.article.application.cqe.label;

import com.lrh.article.application.cqe.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelPageQuery extends PageQuery {

    private String keyword;

}
