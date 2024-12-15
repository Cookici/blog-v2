package com.lrh.blog.user.dao;

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
 * @Package: com.lrh.blog.user.dao
 * @ClassName: User
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class UserModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("user_name")
    private String userName;

    @TableField("user_password")
    private String userPassword;

    @TableField("user_phone")
    private String userPhone;

    @TableField("user_level")
    private Integer userLevel;

    @TableField("user_sex")
    private String userSex;

    @TableField("user_birthday")
    private LocalDateTime userBirthday;

    @TableField("user_ip")
    private String userIp;

    @TableField("role_name")
    private String roleName;

    @TableField("user_email")
    private String userEmail;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;

}
