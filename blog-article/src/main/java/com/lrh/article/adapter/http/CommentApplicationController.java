package com.lrh.article.adapter.http;

import com.lrh.article.application.cqe.comment.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.comment.CommentAdminDTO;
import com.lrh.article.application.dto.comment.CommentDTO;
import com.lrh.article.application.dto.comment.CommentDailyCountDTO;
import com.lrh.article.application.dto.comment.CommentUserDTO;
import com.lrh.article.application.service.CommentApplicationService;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class CommentApplicationController {

    private final CommentApplicationService commentApplicationService;

    public CommentApplicationController(CommentApplicationService commentApplicationService) {
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

    @PostMapping("/insert")
    public Result<Object> insertComment(@RequestBody CommentInsertCommand command) {
        commentApplicationService.insertComment(command);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Object> deletedComment(@RequestBody CommentDeleteCommand command) {
        commentApplicationService.deleteComment(command);
        return Result.success();
    }

    @GetMapping("/user/page")
    public Result<PageDTO<CommentUserDTO>> getUserCommentPage(CommentUserPageQuery query) {
        PageDTO<CommentUserDTO> resp = commentApplicationService.userComment(query);
        return Result.success(resp);
    }

    @GetMapping("/page/all")
    public Result<PageDTO<CommentAdminDTO>> getCommentPageAll(CommentPageAllQuery query) {
        PageDTO<CommentAdminDTO> resp = commentApplicationService.commentPageAll(query);
        return Result.success(resp);
    }

    @GetMapping("/page/child/all")
    public Result<PageDTO<CommentAdminDTO>> getCommentChildPageAll(CommentChildPageAllQuery query) {
        PageDTO<CommentAdminDTO> resp = commentApplicationService.commentChildPageAll(query);
        return Result.success(resp);
    }

    @GetMapping("/get/all/{commentId}")
    public Result<CommentAdminDTO> getCommentByCommentId(@PathVariable("commentId") String commentId) {
        CommentAdminDTO resp = commentApplicationService.getCommentAll(commentId);
        return Result.success(resp);
    }

    @PostMapping("/delete/admin")
    public Result<Object> deleteAdminComment(@RequestBody CommentDeleteAminCommand command) {
        commentApplicationService.deleteAdminComment(command);
        return Result.success();
    }

    @PostMapping("/daily-count")
    public Result<List<CommentDailyCountDTO>> getCommentDailyCount(@RequestBody CommentDateRangeQuery query) {
        List<CommentDailyCountDTO> dailyCountList = commentApplicationService.getCountDailyCount(query);
        return Result.success(dailyCountList);
    }

    @GetMapping("/count")
    public Result<Long> count(){
        return Result.success(commentApplicationService.count());
    }

}
