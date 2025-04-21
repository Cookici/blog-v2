package com.lrh.identity.service;

import com.lrh.identity.dto.resp.UserPermissionResp;

public interface PermissionService {
    /**
     * 获取用户权限信息
     * @param userId 用户ID
     * @return 用户权限响应
     */
    UserPermissionResp getUserPermissions(String userId);

}