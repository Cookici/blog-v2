package com.lrh.identity.vo;

import com.lrh.identity.dao.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.identity.vo
 * @ClassName: RoleVO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO {

    private String roleName;

    private Integer roleLevel;

    public RoleVO convertedRoleModelToRoleVO(RoleModel roleModel) {
        this.roleName = roleModel.getRoleName();
        this.roleLevel = roleModel.getRoleLevel();
        return this;
    }

}
