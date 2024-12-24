package com.lrh.article.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.po
 * @ClassName: CommentPO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 01:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_comment")
public class CommentPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("comment_id")
    private String commentId;

    @TableField("comment_content")
    private String commentContent;

    @TableField("comment_img")
    private String commentImg;

    @TableField("parent_comment_id")
    private String parentCommentId;

    @TableField("user_id")
    private String userId;

    @TableField("to_user_id")
    private String toUserId;

    @TableField("article_id")
    private String articleId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
