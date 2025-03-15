package com.lrh.gateway.client;

import com.lrh.gateway.client.dto.UserPermissionResp;
import com.lrh.gateway.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "blog-identity")
public interface PermissionClient {
    @GetMapping("/api/permission/user")
    Result<UserPermissionResp> getUserPermissions(@RequestParam("userId") String userId);
}
