package com.lrh.article.adapter.http;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.UserDataDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.application.service.ArticleApplicationService;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/get/{articleId}")
    public Result<ArticleDTO> getArticle(@PathVariable("articleId") String articleId) {
        ArticleQuery articleQuery = new ArticleQuery(articleId);
        ArticleDTO articleDTO = articleApplicationService.getArticleById(articleQuery);
        return Result.success(articleDTO);
    }


    @GetMapping("/page")
    public Result<PageDTO<ArticleDTO>> pageArticle(ArticlePageQuery query) {
        PageDTO<ArticleDTO> resp = articleApplicationService.pageArticles(query);
        return Result.success(resp);
    }

    @PostMapping("/delete")
    public Result<Object> deleteArticle(@RequestBody ArticleDeleteCommand command) {
        articleApplicationService.deleteArticleById(command);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Object> updateArticle(@RequestBody ArticleUpdateCommand command) {
        articleApplicationService.updateArticle(command);
        return Result.success();
    }

    @PostMapping("/insert")
    public Result<Object> insertArticle(@RequestBody ArticleInsertCommand command) {
        articleApplicationService.insertArticle(command);
        return Result.success();
    }

    @PostMapping("/view")
    public Result<Object> articleViewIncrement(@RequestBody ArticleViewCommand command) {
        articleApplicationService.articleViewIncrement(command);
        return Result.success();
    }

    @PostMapping("/like")
    public Result<Object> articleLikeIncrement(@RequestBody ArticleLikeCommand command) {
        articleApplicationService.articleLikeIncrement(command);
        return Result.success();
    }

    @PostMapping("/view/no_login")
    public Result<Object> articleNoLoginViewIncrement(@RequestBody ArticleNoLoginViewCommand command, HttpServletRequest request) {
        command.setHttpServletRequest(request);
        articleApplicationService.articleNoLoginViewIncrement(command);
        return Result.success();
    }

    @PostMapping("/like/no_login")
    public Result<Object> articleNoLoginLikeIncrement(@RequestBody ArticleNoLoginLikeCommand command, HttpServletRequest request) {
        command.setHttpServletRequest(request);
        articleApplicationService.articleNoLoginLikeIncrement(command);
        return Result.success();
    }

    @GetMapping("/userArticleData")
    public Result<UserDataDTO> getUserArticleData() {
        UserDataDTO userArticleDataDTO = articleApplicationService.articlesDataByUserId();
        return Result.success(userArticleDataDTO);
    }

    @GetMapping("/user/page")
    public Result<PageDTO<ArticleDTO>> pageUserArticle(ArticleUserPageQuery query) {
        PageDTO<ArticleDTO> resp = articleApplicationService.pageUserArticles(query);
        return Result.success(resp);
    }

    @PostMapping("/like/delete")
    public Result<Object> deleteLike(@RequestBody ArticleDeleteLikeCommand command) {
        articleApplicationService.deleteLike(command);
        return Result.success();
    }

    @GetMapping("/recommend")
    public Result<PageDTO<ArticleDTO>> pageRecommendArticle(ArticleRecommendQuery query) {
        PageDTO<ArticleDTO> resp = articleApplicationService.recommendArticles(query);
        return Result.success(resp);
    }

    @GetMapping("/like/page")
    public Result<PageDTO<ArticleDTO>> pageLikeArticle(ArticleLikePageQuery query) {
        PageDTO<ArticleDTO> resp = articleApplicationService.likeArticles(query);
        return Result.success(resp);
    }


    @GetMapping("/list")
    public Result<Object> listQueryArticle(ArticleListQuery query) {
        PageDTO<ArticleDTO> resp = articleApplicationService.listQueryArticles(query);
        return Result.success(resp);
    }

}
