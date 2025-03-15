package com.lrh.identity.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lrh.identity.constants.UserPermissionConstant;
import com.lrh.identity.dto.ApiDTO;
import com.lrh.identity.dto.ModuleDTO;
import com.lrh.identity.dto.RoleDTO;
import com.lrh.identity.dto.resp.UserPermissionResp;
import com.lrh.identity.mapper.ApiMapper;
import com.lrh.identity.mapper.ModuleMapper;
import com.lrh.identity.mapper.RoleMapper;
import com.lrh.identity.model.ApiModel;
import com.lrh.identity.model.ModuleModel;
import com.lrh.identity.model.RoleModel;
import com.lrh.identity.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    
    private final RoleMapper roleMapper;
    private final ModuleMapper moduleMapper;
    private final ApiMapper apiMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public PermissionServiceImpl(RoleMapper roleMapper, ModuleMapper moduleMapper, 
                               ApiMapper apiMapper, RedisTemplate<String, Object> redisTemplate) {
        this.roleMapper = roleMapper;
        this.moduleMapper = moduleMapper;
        this.apiMapper = apiMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserPermissionResp getUserPermissions(String userId) {
        // 尝试从缓存获取
        Object cachedPermissions = redisTemplate.opsForHash().get(UserPermissionConstant.USER_PERMISSION, userId);
        if (cachedPermissions != null) {
            return JSON.parseObject(JSON.toJSONString(cachedPermissions), UserPermissionResp.class);
        }

        // 获取用户角色
        List<RoleDTO> roles = roleMapper.getUserRoles(userId).stream()
                .map(this::convertToRoleDTO)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            return new UserPermissionResp();
        }

        // 获取用户模块
        List<ModuleDTO> modules = moduleMapper.getUserModules(userId).stream()
                .map(this::convertToModuleDTO)
                .collect(Collectors.toList());

        // 获取每个模块的API
        Map<String, List<ApiDTO>> moduleApis = new HashMap<>();
        modules.forEach(module -> {
            List<ApiDTO> apis = apiMapper.getModuleApis(userId, module.getModuleId()).stream()
                    .map(this::convertToApiDTO)
                    .collect(Collectors.toList());
            moduleApis.put(module.getModuleId(), apis);
        });

        UserPermissionResp permissions = UserPermissionResp.builder()
                .userId(userId)
                .roles(roles)
                .modules(modules)
                .moduleApis(moduleApis)
                .build();

        // 缓存权限数据（2小时过期）
        redisTemplate.opsForHash().put(UserPermissionConstant.USER_PERMISSION, userId, permissions);
        redisTemplate.expire(UserPermissionConstant.USER_PERMISSION, 2, TimeUnit.HOURS);

        return permissions;
    }

    private RoleDTO convertToRoleDTO(RoleModel model) {
        return RoleDTO.builder()
                .roleId(model.getRoleId())
                .roleName(model.getRoleName())
                .roleCode(model.getRoleCode())
                .roleDesc(model.getRoleDesc())
                .build();
    }

    private ModuleDTO convertToModuleDTO(ModuleModel model) {
        return ModuleDTO.builder()
                .moduleId(model.getModuleId())
                .moduleName(model.getModuleName())
                .modulePrefix(model.getModulePrefix())
                .moduleDesc(model.getModuleDesc())
                .parentId(model.getParentId())
                .build();
    }

    private ApiDTO convertToApiDTO(ApiModel model) {
        return ApiDTO.builder()
                .apiId(model.getApiId())
                .moduleId(model.getModuleId())
                .apiName(model.getApiName())
                .apiPath(model.getApiPath())
                .apiMethod(model.getApiMethod())
                .apiDesc(model.getApiDesc())
                .build();
    }
}