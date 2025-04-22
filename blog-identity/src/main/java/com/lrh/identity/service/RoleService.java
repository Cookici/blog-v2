package com.lrh.identity.service;

import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.dto.resp.ModuleApisDTO;

import java.util.List;

public interface RoleService {
    
    /**
     * 绑定用户角色
     * @param req 用户角色绑定请求
     * @return 是否绑定成功
     */
    Boolean bindUserRole(UserRoleBindReq req);
    
    /**
     * 获取用户的角色ID
     * @param userId 用户ID
     * @return 角色ID
     */
    String getRoleIdByUserId(String userId);
    
    /**
     * 获取角色对应的模块和API
     * @param roleId 角色ID
     * @return 模块和API列表
     */
    List<ModuleApisDTO> getRoleModuleApis(String roleId);
}
