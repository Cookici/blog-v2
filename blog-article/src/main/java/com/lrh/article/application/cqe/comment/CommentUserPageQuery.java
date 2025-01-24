package com.lrh.article.application.cqe.comment;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.common.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.cqe.comment
 * @ClassName: CommentUserPageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/21 18:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUserPageQuery extends PageQuery {
    private String userId;

    public void valid() {
        String realUserId = IdUtil.getUserId(userId);
        if (realUserId == null || realUserId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (realUserId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userId = realUserId;
    }
}
