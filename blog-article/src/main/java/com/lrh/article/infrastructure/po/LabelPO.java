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
 * @ClassName: LabelPO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 11:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_label")
public class LabelPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("label_id")
    private String labelId;

    @TableField("label_name")
    private String labelName;

    @TableField("label_alias")
    private String labelAlias;

    @TableField("label_description")
    private String labelDescription;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
