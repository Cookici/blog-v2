package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.comment.CommentChildPageQuery;
import com.lrh.article.application.cqe.comment.CommentPageQuery;
import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.repository.CommentOperateRepository;
import com.lrh.article.infrastructure.database.convertor.CommentConvertor;
import com.lrh.article.infrastructure.po.CommentPO;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
