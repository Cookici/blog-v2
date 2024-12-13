package com.lrh.identity.controller;

import com.lrh.common.result.Result;
import com.lrh.identity.service.RoleService;
import com.lrh.identity.vo.RoleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.identity.controller
 * @ClassName: RoleController
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:06
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/all_kinds")
    public Result<List<RoleVO>> getRoleKinds(){
        List<RoleVO> roleVOList = roleService.getRoleKindsList();
        return Result.success(roleVOList);
    }


}
