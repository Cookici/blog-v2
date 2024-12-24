package com.lrh.article.domain.repository;

import com.lrh.article.infrastructure.po.CommentPO;

import java.util.ArrayList;
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

    List<CommentPO> getChildComments(String parentId);

    List<CommentPO> getCommentsByIds(ArrayList<String> commentIds);

    Long countChildComments(String articleId, String commentId);

    List<CommentPO> selectChildCommentPage(String articleId, String commentId, Long offset, Long limit);
}
