package com.lrh.article.application.cqe.article;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe
 * @ClassName: ArticlePageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageQuery extends PageQuery {

    private String articleTitle;

    private String articleContent;


    public void valid() {
        if (articleTitle != null && articleTitle.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
    }
}
