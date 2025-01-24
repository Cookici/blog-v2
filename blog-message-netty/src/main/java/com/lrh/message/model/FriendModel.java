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
 * @ClassName: FriendModel
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 16:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_friend")
public class FriendModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private String recordId;

    @TableField("user_id")
    private String userId;

    @TableField("friend_id")
    private String friendId;

    @TableField("friend_name")
    private String friendName;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
