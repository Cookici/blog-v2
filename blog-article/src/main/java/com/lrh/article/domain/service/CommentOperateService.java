package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.comment.CommentChildPageQuery;
import com.lrh.article.application.cqe.comment.CommentDeleteCommand;
import com.lrh.article.application.cqe.comment.CommentInsertCommand;
import com.lrh.article.application.cqe.comment.CommentPageQuery;
import com.lrh.article.constants.CommentConstant;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.repository.CommentOperateRepository;
import com.lrh.article.infrastructure.database.convertor.CommentConvertor;
import com.lrh.article.infrastructure.po.CommentPO;
import com.lrh.article.util.LockUtil;
import com.lrh.common.util.IdUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.service
 * @ClassName: CommentOperateService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 01:03
 */
@Service
public class CommentOperateService {

    private final CommentOperateRepository commentOperateRepository;

    private final RedissonClient redissonClient;

    public CommentOperateService(CommentOperateRepository commentOperateRepository, RedissonClient redissonClient) {
        this.commentOperateRepository = commentOperateRepository;
        this.redissonClient = redissonClient;
    }


    public Long countCommentsPage(CommentPageQuery query) {
        return commentOperateRepository.countComments(query.getArticleId());
    }

    public List<CommentEntity> getTopCommentsPage(CommentPageQuery query) {
        List<CommentPO> commentPOList = commentOperateRepository
                .selectParentCommentPage(query.getArticleId(), query.getOffset(), query.getLimit());
        return CommentConvertor.toCommentEntityListConvertor(commentPOList);
    }


    public Long countChildCommentsPage(CommentChildPageQuery query) {
        return commentOperateRepository.countChildComments(query.getArticleId(), query.getCommentId());
    }

    public List<CommentEntity> getChildCommentsPage(CommentChildPageQuery query) {
        List<CommentPO> commentPOList = commentOperateRepository
                .selectChildCommentPage(query.getArticleId(), query.getCommentId(), query.getOffset(), query.getLimit());
        return CommentConvertor.toCommentEntityListConvertor(commentPOList);
    }

    public void insertComment(CommentInsertCommand command) {
        if (!Objects.equals(command.getParentCommentId(), CommentConstant.TOP_COMMENT_PARENT_ID)) {
            insertChildCommentWithLock(command);
        } else {
            insertCommentWithoutLock(command);
        }
    }

    private void insertChildCommentWithLock(CommentInsertCommand command) {
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryLock(String.format(RedisConstant.PARENT_COMMENT_ID_OPERATOR_LOCK, command.getParentCommentId()), () -> insertCommentWithoutLock(command));
    }

    private void insertCommentWithoutLock(CommentInsertCommand command) {
        CommentPO commentPO = CommentPO.builder()
                .commentId("comment_" + IdUtil.getUuid())
                .commentContent(command.getCommentContent())
                .commentImg(command.getCommentImg())
                .parentCommentId(command.getParentCommentId())
                .userId(command.getUserId())
                .toUserId(command.getToUserId())
                .articleId(command.getArticleId())
                .build();
        commentOperateRepository.insertComment(commentPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(CommentDeleteCommand command) {
        if (command.getParentCommentId().equals(CommentConstant.TOP_COMMENT_PARENT_ID)) {
            LockUtil lockUtil = new LockUtil(redissonClient);
            lockUtil.tryLock(String.format(RedisConstant.PARENT_COMMENT_ID_OPERATOR_LOCK, command.getCommentId()), () -> {
                commentOperateRepository.deleteTopComment(command.getArticleId(), command.getCommentId());
                commentOperateRepository.deleteChildComment(command.getArticleId(), command.getCommentId());
            });
        } else {
            commentOperateRepository.deleteComment(command.getArticleId(), command.getParentCommentId(), command.getCommentId());
        }
    }
}
