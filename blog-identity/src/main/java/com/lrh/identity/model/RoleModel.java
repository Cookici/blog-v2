package com.lrh.identity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_role")
public class RoleModel {
    private Long id;
    private String roleId;
    private String roleName;
    private String roleCode;
    private String roleDesc;
    private Integer isDeleted;
}