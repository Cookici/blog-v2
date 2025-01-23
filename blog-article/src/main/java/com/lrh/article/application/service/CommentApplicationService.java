package com.lrh.article.application.service;

import com.lrh.article.application.cqe.comment.CommentChildPageQuery;
import com.lrh.article.application.cqe.comment.CommentDeleteCommand;
import com.lrh.article.application.cqe.comment.CommentInsertCommand;
import com.lrh.article.application.cqe.comment.CommentPageQuery;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.comment.CommentDTO;
import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.service.CommentOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.common.result.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.service
 * @ClassName: CommentApplicationService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:47
 */
@Service
public class CommentApplicationService {

    private final CommentOperateService commentOperateService;

    private final UserClient userClient;

    public CommentApplicationService(CommentOperateService commentOperateService, UserClient userClient) {
        this.commentOperateService = commentOperateService;
        this.userClient = userClient;
    }

    public PageDTO<CommentDTO> pageComments(CommentPageQuery query) {
        query.valid();
        Long total = commentOperateService.countCommentsPage(query);
        if (total == null || total == 0L) {
            return null;
        }

        //获取top的CommentEntity
        List<CommentEntity> commentEntityList = commentOperateService.getTopCommentsPage(query);

        List<CommentDTO> commentDTOList = getFullCommentList(commentEntityList);

        List<CommentDTO> results = commentDTOList.stream().
                filter(commentDTO -> Objects.equals(commentDTO.getParentCommentId(), "0"))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        return PageDTO.<CommentDTO>builder()
                .total(total)
                .data(results)
                .page(query.getPage())
                .pageSize(query.getPageSize())
                .build();
    }

    public PageDTO<CommentDTO> pageChildComments(CommentChildPageQuery query) {
        query.valid();
        Long total = commentOperateService.countChildCommentsPage(query);
        if (total == null || total == 0L) {
            return null;
        }

        List<CommentEntity> commentEntityList = commentOperateService.getChildCommentsPage(query);

        List<CommentDTO> commentDTOList = getFullCommentList(commentEntityList);

        return PageDTO.<CommentDTO>builder()
                .total(total)
                .data(commentDTOList)
                .page(query.getPage())
                .pageSize(query.getPageSize())
                .build();
    }

    /**
     * 获取完整的评论列表
     *
     * @param commentEntityList 没有填充用户信息的评论列表
     * @return List<CommentDTO>
     */
    private List<CommentDTO> getFullCommentList(List<CommentEntity> commentEntityList) {
        List<String> uniqueUserIdList = Stream.concat(
                commentEntityList.stream().map(CommentEntity::getUserId),
                commentEntityList.stream().map(CommentEntity::getToUserId)
        ).distinct().collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(uniqueUserIdList);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<CommentDTO> commentDTOList = new ArrayList<>();
        commentEntityList.forEach(commentEntity -> {
            UserVO userVO = userIdForUser.getOrDefault(commentEntity.getUserId(),new UserVO());
            UserVO toUserVO = userIdForUser.getOrDefault(commentEntity.getToUserId(), new UserVO());
            CommentDTO commentDTO = CommentDTO.fromEntity(commentEntity, userVO, toUserVO);
            commentDTOList.add(commentDTO);
        });
        return commentDTOList;
    }

    public void insertComment(CommentInsertCommand command) {
        command.valid();
        commentOperateService.insertComment(command);
    }

    public void deleteComment(CommentDeleteCommand command) {
        command.valid();
        commentOperateService.deleteComment(command);
    }
}
