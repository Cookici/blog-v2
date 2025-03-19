package com.lrh.article.domain.repository;

import com.lrh.article.application.dto.comment.CommentDTO;

import java.util.List;

public interface CommentCacheRepository {

    void saveCommentCount(String key, Long count);
    Long getCommentCount(String key);

    void saveTopComments(String articleId, Long page, Long pageSize, List<CommentDTO> comments);
    List<CommentDTO> getTopComments(String articleId, Long page, Long pageSize);

    void saveChildComments(String articleId, String parentCommentId, Long page, Long pageSize, List<CommentDTO> comments);
    List<CommentDTO> getChildComments(String articleId, String parentCommentId, Long page, Long pageSize);

    void saveUserComments(String userId, Long page, Long pageSize, List<CommentDTO> comments);
    List<CommentDTO> getUserComments(String userId, Long page, Long pageSize);

    void deleteCommentCache(String articleId);
    void deleteCommentChildCache(String articleId, String parentCommentId);
    void deleteUserCommentCache(String userId);

    void deleteChildCommentCountCache(String articleId, String commentId);

    void deleteAllChildCommentsCache(String articleId, String commentId);

}
