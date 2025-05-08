package com.lrh.article.application.cqe.article;

import com.lrh.article.domain.vo.ArticleStatusEnum;
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
public class ArticleChangeStatusCommand {
    private String articleId;
    private String status;
    private String userName;

    public void valid() {
        if (this.articleId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }

        if (this.status == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (!this.status.equals(ArticleStatusEnum.Published.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.UnderAudit.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.Deleted.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.FailedAudit.getStatus())
                ) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }

    }
}
