package com.lrh.article.domain.repository;

import com.lrh.article.infrastructure.po.CommentPO;

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
}
