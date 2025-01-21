package com.lrh.article.application.cqe.comment;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.common.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe.comment
 * @ClassName: CommentDeleteCommand
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/25 16:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDeleteCommand {
    private String userId;
    private String articleId;
    private String commentId;
    private String parentCommentId;

    public void valid() {
        if (this.articleId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        String realUserId = IdUtil.getUserId(userId);
        if (realUserId == null || realUserId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (realUserId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userId = realUserId;
        if (this.commentId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.commentId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.parentCommentId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.parentCommentId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }
}
