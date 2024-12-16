package com.lrh.article.application.cqe.article;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 19:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleQuery {
    private String articleId;

    public void valid(){
        if(this.articleId == null || this.articleId.length() > 128){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR,"校验失败"));
        }
    }
}
