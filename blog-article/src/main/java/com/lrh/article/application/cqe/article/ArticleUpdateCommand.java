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
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleUpdateCommand
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/16 11:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleUpdateCommand {

    private String userId;

    private String articleId;

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
        if (articleId == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (labelIdList == null) {
            labelIdList = new ArrayList<>();
        } else {
            labelIdList = labelIdList.stream()
                    .filter(labelId -> labelId != null && !labelId.trim().isEmpty())
                    .collect(Collectors.toList());
        }
        if (articleTitle != null) {
            if (articleTitle.length() > BusinessConstant.ID_MAX_LENGTH || articleTitle.trim().isEmpty()) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
            }
        }
        if (articleContent != null && articleContent.trim().isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "文章标题"));
        }
    }
}
