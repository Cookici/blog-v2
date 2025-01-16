package com.lrh.article.application.cqe.article;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.exception.ValidException;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleViewIncrementCommand
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/15 17:33
 */
@Data
@NoArgsConstructor
public class ArticleViewCommand {

    private String articleId;

    public void valid() {
        if (articleId == null) {
            throw new ValidException("校验失败");
        }
        if (articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException("校验失败");
        }
        if (UserContext.getUserId() == null) {
            throw new ValidException("校验失败");
        }
        if (UserContext.getUserId().length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException("校验失败");
        }
    }

}
