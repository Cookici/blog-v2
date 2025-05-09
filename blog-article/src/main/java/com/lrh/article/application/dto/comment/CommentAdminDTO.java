package com.lrh.article.application.dto.comment;

import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAdminDTO {
    private String commentId;

    private String commentContent;

    private String commentImg;

    private String parentCommentId;

    private String userId;

    private String articleId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private UserVO userInfo;

    private UserVO toUserInfo;

    private Integer isDeleted;

    public static CommentAdminDTO fromEntity(CommentEntity entity, UserVO userInfo, UserVO toUserInfo) {
        return CommentAdminDTO.builder()
                .commentId(entity.getCommentId())
                .commentContent(entity.getCommentContent())
                .commentImg(entity.getCommentImg())
                .parentCommentId(entity.getParentCommentId())
                .userId(entity.getUserId())
                .articleId(entity.getArticleId())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .userInfo(userInfo)
                .toUserInfo(toUserInfo)
                .isDeleted(entity.getIsDeleted())
                .build();
    }
}
