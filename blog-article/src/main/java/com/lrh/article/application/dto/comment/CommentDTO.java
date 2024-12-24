package com.lrh.article.application.dto.comment;

import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.dto.comment
 * @ClassName: CommentDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 01:20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private String commentId;

    private String commentContent;

    private String commentImg;

    private String parentCommentId;

    private List<CommentDTO> commentList;

    private String userId;

    private String articleId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private UserVO userInfo;

    private UserVO toUserInfo;

    public static CommentDTO fromEntity(CommentEntity entity, UserVO userInfo,UserVO toUserInfo) {
        return CommentDTO.builder()
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
                .build();
    }
}
