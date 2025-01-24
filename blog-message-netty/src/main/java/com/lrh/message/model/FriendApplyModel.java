package com.lrh.message.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.model
 * @ClassName: FriendApplyModel
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_friend_apply")
public class FriendApplyModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private String recordId;

    /**
     * 申请人id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 被申请人id
     */
    @TableField("applied_id")
    private String appliedId;

    @TableField("apply_status")
    private String applyStatus;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
