package com.lrh.identity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.model.RoleModel;

public interface RoleService extends IService<RoleModel> {
    Boolean bindUserRole(UserRoleBindReq req);
}
