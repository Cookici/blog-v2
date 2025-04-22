package com.lrh.blog.user.romote;

import com.lrh.blog.user.romote.dto.req.UserRoleBindReq;
import com.lrh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "blog-identity")
public interface RoleClient {
    @PostMapping("/api/role/bind")
    Result<Boolean> bindUserRole(UserRoleBindReq req);

    @GetMapping("/api/role/get")
    Result<String> getRolesByUserId(@RequestParam("user_id") String userId);
}
