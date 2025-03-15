package com.lrh.identity.controller;

import com.lrh.common.result.Result;
import com.lrh.identity.dto.resp.UserPermissionResp;
import com.lrh.identity.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/user")
    public Result<UserPermissionResp> getUserPermissions(@RequestParam("userId") String userId) {
        UserPermissionResp permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }
}