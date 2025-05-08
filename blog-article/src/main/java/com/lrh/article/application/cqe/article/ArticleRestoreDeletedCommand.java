package com.lrh.article.application.cqe.article;

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
public class ArticleRestoreDeletedCommand {
    private String articleId;

    public void valid() {
        if (articleId == null) {
            throw new ValidException("校验失败");
        }
        if (articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException("校验失败");
        }
    }

}
