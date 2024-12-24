package com.lrh.article.application.cqe.comment;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe.comment
 * @ClassName: CommentPageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:50
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageQuery extends PageQuery {

    private String articleId;

    public void valid() {
        if (articleId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (articleId.isEmpty() || articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }

}
