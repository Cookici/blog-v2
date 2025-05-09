package com.lrh.article.application.cqe.comment;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDeleteAminCommand {

    private String commentId;
    private String parentCommentId;

    public void valid() {
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
