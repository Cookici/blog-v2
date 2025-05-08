package com.lrh.article.application.cqe.article;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.article.domain.vo.ArticleStatusEnum;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageAllQuery extends PageQuery {

    private String articleTitle;

    private String articleContent;

    private Integer isDeleted;

    private String status;


    public void valid() {
        if (articleTitle != null && articleTitle.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
        if (this.status != null &&
                !this.status.equals(ArticleStatusEnum.Published.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.UnderAudit.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.Deleted.getStatus()) &&
                !this.status.equals(ArticleStatusEnum.FailedAudit.getStatus())
        ) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.isDeleted != null &&
                !this.isDeleted.equals(BusinessConstant.IS_DELETED) &&
                !this.isDeleted.equals(BusinessConstant.IS_NOT_DELETED)) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }

}
