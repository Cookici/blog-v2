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
 * @ClassName: ArticleLabelPO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_article_label")
public class ArticleLabelPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("article_id")
    private String articleId;

    @TableField("label_id")
    private String labelId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
