package com.lrh.article.application.cqe.article;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe
 * @ClassName: ArticlePageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageQuery extends PageQuery {

    private String articleTitle;

    private String articleContent;

    private List<String> labelNameList;


    public void valid(){
        if (articleTitle != null && articleTitle.length() > 64) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"文章标题"));
        }
        if (labelNameList == null){
            labelNameList = new ArrayList<>();
        }
        for (String label : labelNameList) {
            if (label == null || label.trim().isEmpty()) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"标签信息"));
            }
            if (label.length() > 64) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"标签信息"));
            }
        }
    }

}
