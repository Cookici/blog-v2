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
 * @ClassName: CommentChildPageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/25 01:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentChildPageQuery extends PageQuery {
    private String articleId;

    private String commentId;

    public void valid() {
        if (articleId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (articleId.isEmpty() || articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }

        if (commentId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (commentId.isEmpty() || commentId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }
}
