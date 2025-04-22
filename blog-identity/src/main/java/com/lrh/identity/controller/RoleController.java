package com.lrh.identity.controller;

import com.lrh.common.result.Result;
import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.dto.resp.ModuleApisDTO;
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

    @GetMapping("/get")
    public Result<String> getRoleByUserId(@RequestParam("user_id") String userId) {
        return Result.success(roleService.getRoleIdByUserId(userId));
    }

    @GetMapping("/apis")
    public Result<List<ModuleApisDTO>> getRoleApis(@RequestParam("role") String roleId) {
        return Result.success(roleService.getRoleModuleApis(roleId));
    }
}
