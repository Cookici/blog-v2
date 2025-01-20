package com.lrh.article.application.cqe.article;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.common.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleInsertCommand
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/16 11:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleInsertCommand {

    private String userId;

    private List<String> labelIdList;

    private String articleTitle;

    private String articleContent;


    public void valid() {
        String realUserId = IdUtil.getUserId(userId);
        if (realUserId == null || realUserId.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (realUserId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userId = realUserId;
        if (labelIdList == null) {
            labelIdList = new ArrayList<>();
        }
        if (articleTitle == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
        if (articleTitle.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
        if (articleContent == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章内容"));
        }

    }
}
