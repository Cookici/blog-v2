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
 * @ClassName: CommentInsertCommand
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/25 16:01
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentInsertCommand {

    private String commentContent;

    private String commentImg;

    private String parentCommentId;

    private String userId;

    private String articleId;

    private String toUserId;

    public void valid() {
        if(commentContent == null){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"评论内容"));
        }
        if(commentContent.isEmpty()){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"评论内容"));
        }
        if(commentImg != null && commentContent.length() > 256){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }
        if(parentCommentId != null && parentCommentId.length() > BusinessConstant.ID_MAX_LENGTH){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }
        String realUserId = IdUtil.getUserId(userId);
        if (realUserId == null || realUserId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (realUserId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userId = realUserId;
        if(articleId == null){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }
        if(articleId.length() > BusinessConstant.ID_MAX_LENGTH){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }
        if(toUserId != null && toUserId.length() > BusinessConstant.ID_MAX_LENGTH){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }


    }
}
