package com.lrh.article.domain.entity;

import com.lrh.article.infrastructure.po.CommentPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.entity
 * @ClassName: CommentEntity
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 01:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    private String commentId;

    private String commentContent;

    private String commentImg;

    private String parentCommentId;

    private String userId;

    private String toUserId;

    private String articleId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static CommentEntity fromPO(CommentPO commentPO) {
        if (commentPO == null) {
            return null;
        }
        return CommentEntity.builder()
                .commentId(commentPO.getCommentId())
                .commentContent(commentPO.getCommentContent())
                .commentImg(commentPO.getCommentImg())
                .parentCommentId(commentPO.getParentCommentId())
                .userId(commentPO.getUserId())
                .toUserId(commentPO.getToUserId())
                .articleId(commentPO.getArticleId())
                .createTime(commentPO.getCreateTime())
                .updateTime(commentPO.getUpdateTime())
                .build();
    }

}
