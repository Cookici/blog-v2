package com.lrh.identity.controller;

import com.lrh.common.result.Result;
import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.model.RoleModel;
import com.lrh.identity.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    
    private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @PostMapping("/bind")
    public Result<Boolean> bindUserRole(@RequestBody UserRoleBindReq req) {
        return Result.success(roleService.bindUserRole(req));
    }
}
