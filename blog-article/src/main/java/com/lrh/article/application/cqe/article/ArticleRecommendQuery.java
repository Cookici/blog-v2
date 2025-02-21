package com.lrh.article.application.cqe.article;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.common.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleRecommendPageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 21:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRecommendQuery extends PageQuery {

    private String userId;

    private String articleTitle;

    private String articleContent;

    private List<String> labelNameList;

    public void valid() {
        String realUserId = IdUtil.getUserId(userId);
        if (realUserId == null || realUserId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (realUserId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userId = realUserId;
        if (articleTitle != null && articleTitle.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
        if (labelNameList == null) {
            labelNameList = new ArrayList<>();
        }
        for (String label : labelNameList) {
            if (label == null || label.trim().isEmpty()) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "标签信息"));
            }
            if (label.length() > 64) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "标签信息"));
            }
        }
    }

}
