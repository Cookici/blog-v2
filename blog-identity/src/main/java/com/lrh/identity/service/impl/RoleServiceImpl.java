package com.lrh.identity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.mapper.RoleMapper;
import com.lrh.identity.model.RoleModel;
import com.lrh.identity.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleModel> implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindUserRole(UserRoleBindReq req) {
        Integer integer = roleMapper.insertUserRole(req.getUserId(),req.getRoleId());
        return integer > 0;
    }
}
