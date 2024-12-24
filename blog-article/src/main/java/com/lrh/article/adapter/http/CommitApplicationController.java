package com.lrh.article.adapter.http;

import com.lrh.article.application.cqe.comment.CommentChildPageQuery;
import com.lrh.article.application.cqe.comment.CommentPageQuery;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.comment.CommentDTO;
import com.lrh.article.application.service.CommentApplicationService;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.adapter.http
 * @ClassName: CommitApplicationController
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 19:33
 */
@RestController
@RequestMapping("/api/comment")
public class CommitApplicationController {

    private final CommentApplicationService commentApplicationService;

    public CommitApplicationController(CommentApplicationService commentApplicationService) {
        this.commentApplicationService = commentApplicationService;
    }

    @GetMapping("/page")
    public Result<PageDTO<CommentDTO>> pageComment(CommentPageQuery query) {
        PageDTO<CommentDTO> resp = commentApplicationService.pageComments(query);
        return Result.success(resp);
    }

    @GetMapping("/page_child")
    public Result<PageDTO<CommentDTO>> pageChildComment(CommentChildPageQuery query) {
        PageDTO<CommentDTO> resp = commentApplicationService.pageChildComments(query);
        return Result.success(resp);
    }

}
