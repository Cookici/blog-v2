package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.application.dto.comment.CommentDailyCountDTO;
import com.lrh.article.constants.CommentConstant;
import com.lrh.article.domain.repository.CommentOperateRepository;
import com.lrh.article.infrastructure.database.mapper.CommentMapper;
import com.lrh.article.infrastructure.po.CommentPO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Override
    public void insertComment(CommentPO commentPO) {
        commentMapper.insert(commentPO);
    }

    @Override
    public void deleteTopComment(String articleId, String commentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getParentCommentId, CommentConstant.TOP_COMMENT_PARENT_ID)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public void deleteChildComment(String articleId, String parentCommentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getParentCommentId, parentCommentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public void deleteComment(String articleId, String parentCommentId, String commentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getParentCommentId, parentCommentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public void deleteCommentsByArticle(String articleId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public Long getUserCommentAsTo(String userId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getToUserId, userId)
                .ne(CommentPO::getUserId, userId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public CommentPO getCommentByCommentId(String commentId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectOne(queryWrapper);
    }

    @Override
    public Long countUserCommentsPage(String userId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getUserId, userId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CommentPO> getUserCommentPage(String userId, Long offset, Long limit) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getUserId, userId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .orderByDesc(CommentPO::getCreateTime)
                .last("limit " + offset + "," + limit);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public CommentPO selectParentCommentByCommentId(String commentId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return commentMapper.selectOne(queryWrapper);
    }

    @Override
    public void restoreCommentByArticleId(String articleId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getArticleId, articleId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public Long countCommentPageAll(String keyword) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .like(CommentPO::getCommentContent, keyword);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CommentPO> pageCommentAll(String keyword,Long limit, Long offset) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .like(CommentPO::getCommentContent, keyword)
                .orderByDesc(CommentPO::getCreateTime)
                .last("limit " + offset + ", " + limit);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public Long countCommentChildAll(String commentId,String keyword) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getParentCommentId, commentId)
                .like(CommentPO::getCommentContent, keyword);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CommentPO> pageChildCommentAll(String keyword,String parentCommentId, Long limit, Long offset) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getParentCommentId, parentCommentId)
                .like(CommentPO::getCommentContent, keyword)
                .orderByDesc(CommentPO::getCreateTime)
                .last("limit " + offset + ", " + limit);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public CommentPO getCommentByCommentIdAll(String commentId) {
        LambdaQueryWrapper<CommentPO> queryWrapper = Wrappers.lambdaQuery(CommentPO.class)
                .eq(CommentPO::getCommentId, commentId);
        return commentMapper.selectOne(queryWrapper);
    }

    @Override
    public void deleteCommentAdmin(String commentId, String parentCommentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getParentCommentId, parentCommentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public void deleteChildCommentAdmin(String commentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getParentCommentId, commentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public void deleteTopCommentAdmin(String commentId) {
        LambdaUpdateWrapper<CommentPO> updateWrapper = Wrappers.lambdaUpdate(CommentPO.class)
                .eq(CommentPO::getCommentId, commentId)
                .eq(CommentPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(CommentPO::getIsDeleted, BusinessConstant.IS_DELETED);
        commentMapper.update(updateWrapper);
    }

    @Override
    public List<CommentDailyCountDTO> getCommentDailyCount(LocalDateTime startDate, LocalDateTime endDate) {
        return commentMapper.selectCommentDailyCount(startDate, endDate);
    }

    @Override
    public Long count() {
        return commentMapper.selectCount(null);
    }

}
