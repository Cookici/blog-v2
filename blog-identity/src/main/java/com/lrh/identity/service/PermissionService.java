package com.lrh.identity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.identity.dto.resp.UserPermissionResp;

public interface PermissionService {
    UserPermissionResp getUserPermissions(String userId);
}