package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.PageQuery;
import com.lrh.article.application.cqe.comment.*;
import com.lrh.article.application.dto.comment.CommentDailyCountDTO;
import com.lrh.article.constants.CommentConstant;
import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.repository.CommentOperateRepository;
import com.lrh.article.infrastructure.database.convertor.CommentConvertor;
import com.lrh.article.infrastructure.po.CommentPO;
import com.lrh.common.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    public CommentOperateService(CommentOperateRepository commentOperateRepository) {
        this.commentOperateRepository = commentOperateRepository;
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


    /**
     * 插入评论 - 不使用锁的版本，由应用服务层控制锁
     */
    public String insertComment(CommentInsertCommand command) {
        CommentPO commentPO = getCommentPO(command);
        
        if (!Objects.equals(command.getParentCommentId(), CommentConstant.TOP_COMMENT_PARENT_ID)) {
            // 检查父评论是否存在
            CommentPO commentParentPO = commentOperateRepository.selectParentCommentByCommentId(command.getParentCommentId());
            if (commentParentPO == null) {
                throw new RuntimeException("评论已经删除");
            }
        }
        
        commentOperateRepository.insertComment(commentPO);
        return commentPO.getCommentId();
    }

    /**
     * 删除评论 - 不使用锁的版本，由应用服务层控制锁
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(CommentDeleteCommand command) {
        validExceptionOperate(command.getCommentId(), command.getUserId());
        
        if (command.getParentCommentId().equals(CommentConstant.TOP_COMMENT_PARENT_ID)) {
            // 删除顶级评论及其所有子评论
            commentOperateRepository.deleteTopComment(command.getArticleId(), command.getCommentId());
            commentOperateRepository.deleteChildComment(command.getArticleId(), command.getCommentId());
        } else {
            // 只删除子评论
            commentOperateRepository.deleteComment(command.getArticleId(), command.getParentCommentId(), command.getCommentId());
        }
    }

    private static CommentPO getCommentPO(CommentInsertCommand command) {
        CommentPO commentPO = CommentPO.builder()
                .commentId("comment_" + IdUtil.getUuid())
                .commentContent(command.getCommentContent())
                .commentImg(command.getCommentImg())
                .parentCommentId(command.getParentCommentId())
                .userId(command.getUserId())
                .toUserId(command.getToUserId())
                .articleId(command.getArticleId())
                .build();
        return commentPO;
    }



    private void validExceptionOperate(String commentId, String userId) {
        CommentPO commentPO = commentOperateRepository.getCommentByCommentId(commentId);
        if (commentPO == null || !Objects.equals(commentPO.getUserId(), userId)) {
            throw new RuntimeException("非法操作");
        }
    }

    public Long getUserCommentAsTo(String userId) {
        return commentOperateRepository.getUserCommentAsTo(userId);
    }

    public Long countUserCommentsPage(CommentUserPageQuery query) {
        return commentOperateRepository.countUserCommentsPage(query.getUserId());
    }

    public List<CommentEntity> getUserComment(CommentUserPageQuery query) {
        List<CommentPO> commentPOList =
                commentOperateRepository.getUserCommentPage(query.getUserId(), query.getOffset(), query.getLimit());
        return CommentConvertor.toCommentEntityListConvertor(commentPOList);
    }

    public Long countCommentPageAll() {
        return commentOperateRepository.countCommentPageAll();
    }

    public List<CommentEntity> commentPageAll(PageQuery query) {
        List<CommentPO> commentPOList = commentOperateRepository.pageCommentAll(query.getLimit(), query.getOffset());
        return CommentConvertor.toCommentEntityListConvertor(commentPOList);
    }

    public Long countCommentChildPageAll(CommentChildPageAllQuery query) {
        return commentOperateRepository.countCommentChildAll(query);
    }

    public List<CommentEntity> commentChildPageAll(CommentChildPageAllQuery query) {
        List<CommentPO> commentPOList = commentOperateRepository.pageChildCommentAll(query.getCommentId(),query.getLimit(), query.getOffset());
        return CommentConvertor.toCommentEntityListConvertor(commentPOList);
    }

    public CommentEntity getCommentAll(String commentId) {
        CommentPO commentPO = commentOperateRepository.getCommentByCommentIdAll(commentId);
        return CommentEntity.fromPO(commentPO);
    }

    public void deleteCommentAdmin(CommentDeleteAminCommand command) {
        if (command.getParentCommentId().equals(CommentConstant.TOP_COMMENT_PARENT_ID)) {
            commentOperateRepository.deleteTopCommentAdmin(command.getCommentId());
            commentOperateRepository.deleteChildCommentAdmin(command.getCommentId());
        } else {
            commentOperateRepository.deleteCommentAdmin(command.getCommentId(),command.getParentCommentId());
        }

    }

    public CommentEntity getCommentByCommentId(String commentId) {
        CommentPO commentByCommentId = commentOperateRepository.getCommentByCommentId(commentId);
        return CommentEntity.fromPO(commentByCommentId);
    }

    public List<CommentDailyCountDTO> getCommentDailyCount(LocalDateTime startDate, LocalDateTime endDate) {
        return commentOperateRepository.getCommentDailyCount(startDate, endDate);
    }

    public Long count() {
        return commentOperateRepository.count();
    }
}
