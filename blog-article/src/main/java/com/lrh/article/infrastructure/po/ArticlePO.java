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
 * @ClassName: Article
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:17
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_article")
public class ArticlePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("article_id")
    private String articleId;

    @TableField("user_id")
    private String userId;

    @TableField("article_title")
    private String articleTitle;

    @TableField("article_content")
    private String articleContent;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("status")
    private String status;

    @TableField("is_deleted")
    private Integer isDeleted;
}