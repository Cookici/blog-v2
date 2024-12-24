package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.constants.CommentConstant;
import com.lrh.article.domain.repository.CommentOperateRepository;
import com.lrh.article.infrastructure.database.mapper.CommentMapper;
import com.lrh.article.infrastructure.po.CommentPO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.repository
 * @ClassName: CommentOperateRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:43
 */
@Repository
public class CommentRepositoryImpl implements CommentOperateRepository {

    private final CommentMapper commentMapper;

    public CommentRepositoryImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Long countComments(String articleId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .eq(CommentPO::getParentCommentId, CommentConstant.TOP_COMMENT_PARENT_ID);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CommentPO> selectParentCommentPage(String articleId, Long offset, Long limit) {
        return commentMapper.selectParentCommentPage(articleId, offset, limit);
    }

    @Override
    public List<CommentPO> getChildComments(String parentId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getParentCommentId, parentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public List<CommentPO> getCommentsByIds(ArrayList<String> commentIds) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .in(CommentPO::getCommentId, commentIds)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public Long countChildComments(String articleId, String commentId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getParentCommentId, commentId)
                .ne(CommentPO::getParentCommentId, CommentConstant.TOP_COMMENT_PARENT_ID)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CommentPO> selectChildCommentPage(String articleId, String commentId, Long offset, Long limit) {
        return commentMapper.selectChildCommentPage(articleId, commentId, offset, limit);
    }

}
