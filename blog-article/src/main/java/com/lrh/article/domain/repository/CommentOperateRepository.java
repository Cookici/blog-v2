package com.lrh.article.domain.repository;

import com.lrh.article.application.cqe.comment.CommentChildPageAllQuery;
import com.lrh.article.application.dto.comment.CommentDailyCountDTO;
import com.lrh.article.infrastructure.po.CommentPO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.repository
 * @ClassName: CommentOperateReposity
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:42
 */

public interface CommentOperateRepository {

    Long countComments(String articleId);

    List<CommentPO> selectParentCommentPage(String articleId,Long offset,Long limit);

    Long countChildComments(String articleId, String commentId);

    List<CommentPO> selectChildCommentPage(String articleId, String commentId, Long offset, Long limit);

    void insertComment(CommentPO commentPO);

    void deleteTopComment(String articleId, String commentId);

    void deleteChildComment(String articleId, String parentCommentId);

    void deleteComment(String articleId, String parentCommentId, String commentId);

    void deleteCommentsByArticle(String articleId);

    Long getUserCommentAsTo(String userId);

    CommentPO getCommentByCommentId(String commentId);

    Long countUserCommentsPage(String userId);

    List<CommentPO> getUserCommentPage(String userId, Long offset, Long limit);

    CommentPO selectParentCommentByCommentId(String commentId);

    void restoreCommentByArticleId(String articleId);

    Long countCommentPageAll();

    List<CommentPO> pageCommentAll(Long limit, Long offset);

    Long countCommentChildAll(CommentChildPageAllQuery query);

    List<CommentPO> pageChildCommentAll(String commentId, Long limit, Long offset);

    CommentPO getCommentByCommentIdAll(String commentId);

    void deleteCommentAdmin(String commentId, String parentCommentId);

    void deleteChildCommentAdmin(String parentCommentId);

    void deleteTopCommentAdmin(String commentId);

    List<CommentDailyCountDTO> getCommentDailyCount(LocalDateTime startDate, LocalDateTime endDate);

    Long count();
}
