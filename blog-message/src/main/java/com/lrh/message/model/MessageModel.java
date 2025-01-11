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
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.model
 * @ClassName: MessageModel
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/9 22:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_message")
public class MessageModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("message_id")
    private String messageId;

    @TableField("message_type")
    private String messageType;

    @TableField("message_content")
    private String messageContent;

    @TableField("user_id")
    private String userId;

    @TableField("to_user_id")
    private String toUserId;

    @TableField("timestamp")
    private Long timestamp;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;

}
