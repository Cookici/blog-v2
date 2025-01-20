package com.lrh.article.application.cqe.article;


import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListQuery extends PageQuery {
    private String element;

    public void valid() {
        if (element == null || element.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "元素"));
        }
    }
}
