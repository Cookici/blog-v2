package com.lrh.article.adapter.http;

import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.application.dto.article.ArticlePageDTO;
import com.lrh.article.application.service.ArticleApplicationService;
import com.lrh.common.exception.ValidException;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.adapter.http
 * @ClassName: ArticleController
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 23:20
 */
@RestController
@RequestMapping("/api/article")
public class ArticleApplicationController {

    private final ArticleApplicationService articleApplicationService;

    public ArticleApplicationController(ArticleApplicationService articleApplicationService) {
        this.articleApplicationService = articleApplicationService;
    }


    @GetMapping
    public Result<ArticlePageDTO> pageArticle(ArticlePageQuery query) throws ValidException {
        ArticlePageDTO articlePageDTO = articleApplicationService.pageArticles(query);
        return Result.success(articlePageDTO);
    }

}
