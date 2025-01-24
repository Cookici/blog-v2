package com.lrh.article.application.dto.comment;

import com.lrh.article.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName:    blog-v2 
 * @Package:        com.lrh.article.application.dto.comment
 * @ClassName:      CommentUserDTO
 * @Author:     63283
 * @Description:    
 * @Date:    2025/1/21 19:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUserDTO {
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

    private String articleTitle;

    public static CommentUserDTO fromCommentDTO(CommentDTO commentDTO, String articleTitle) {
        return CommentUserDTO.builder()
                .commentId(commentDTO.getCommentId())
                .commentContent(commentDTO.getCommentContent())
                .commentImg(commentDTO.getCommentImg())
                .parentCommentId(commentDTO.getParentCommentId())
                .userId(commentDTO.getUserId())
                .articleId(commentDTO.getArticleId())
                .createTime(commentDTO.getCreateTime())
                .updateTime(commentDTO.getUpdateTime())
                .userInfo(commentDTO.getUserInfo())
                .toUserInfo(commentDTO.getToUserInfo())
                .articleTitle(articleTitle)
                .build();
    }
}