package com.lrh.article.application.cqe.article;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Builder
public class ArticlePageQuery {

    private String articleTitle;

    private String articleContent;

    private PageQuery pageQuery;

    public void valid() throws ValidException {
        if (this.articleContent.isEmpty()){
            throw new ValidException("文章内容");
        }
        if (this.articleTitle.isEmpty()){
            throw new ValidException("文章标题");
        }
    }

}
