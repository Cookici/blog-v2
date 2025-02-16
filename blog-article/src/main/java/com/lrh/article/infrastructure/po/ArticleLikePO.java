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
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.po
 * @ClassName: ArticleLikePO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 20:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_article_like")
public class ArticleLikePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private String recordId;

    @TableField("article_id")
    private String articleId;

    @TableField("user_id")
    private String userId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
