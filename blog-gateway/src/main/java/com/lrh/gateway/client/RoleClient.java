package com.lrh.gateway.client;

import com.lrh.gateway.client.dto.ModuleApisDTO;
import com.lrh.gateway.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "blog-identity")
public interface RoleClient {
    @GetMapping("/api/role/apis")
    Result<List<ModuleApisDTO>> getRoleApis(@RequestParam("role") String roleId);
}
