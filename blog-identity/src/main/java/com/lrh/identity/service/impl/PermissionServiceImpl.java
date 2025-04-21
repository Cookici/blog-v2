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

import java.util.ArrayList;
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
        log.info("[PermissionServiceImpl] getUserPermissions 开始获取用户权限: {}", userId);
        
        // 1. 获取用户角色（先从缓存获取，缓存未命中则从数据库获取）
        String userRolesCacheKey = UserPermissionConstant.USER_ROLES + ":" + userId;
        List<RoleDTO> roles;
        Object cachedRoles = redisTemplate.opsForValue().get(userRolesCacheKey);
        
        if (cachedRoles != null) {
            log.info("[PermissionServiceImpl] getUserPermissions 从缓存获取用户角色: {}", userId);
            roles = JSON.parseArray(JSON.toJSONString(cachedRoles), RoleDTO.class);
        } else {
            log.info("[PermissionServiceImpl] getUserPermissions 从数据库获取用户角色: {}", userId);
            // 获取所有角色和用户角色ID，在内存中筛选
            List<RoleModel> allRoles = roleMapper.getAllRoles();
            List<String> userRoleIds = roleMapper.getUserRoleIds(userId);
            
            roles = allRoles.stream()
                    .filter(role -> userRoleIds.contains(role.getRoleId()))
                    .map(this::convertToRoleDTO)
                    .collect(Collectors.toList());
            
            // 缓存用户角色（1小时过期）
            if (!roles.isEmpty()) {
                redisTemplate.opsForValue().set(userRolesCacheKey, roles, 1, TimeUnit.HOURS);
            }
        }
        
        if (roles.isEmpty()) {
            log.info("[PermissionServiceImpl] getUserPermissions 用户没有角色权限: {}", userId);
            return new UserPermissionResp();
        }
        
        // 2. 获取所有模块和API（先从缓存获取，缓存未命中则从数据库获取）
        // 获取所有模块
        String allModulesCacheKey = UserPermissionConstant.ALL_MODULES;
        List<ModuleModel> allModules;
        Object cachedModules = redisTemplate.opsForValue().get(allModulesCacheKey);
        
        if (cachedModules != null) {
            log.info("[PermissionServiceImpl] getUserPermissions 从缓存获取所有模块");
            allModules = JSON.parseArray(JSON.toJSONString(cachedModules), ModuleModel.class);
        } else {
            log.info("[PermissionServiceImpl] getUserPermissions 从数据库获取所有模块");
            allModules = moduleMapper.getAllModules();
            // 缓存所有模块（24小时过期，因为模块变动较少）
            if (!allModules.isEmpty()) {
                redisTemplate.opsForValue().set(allModulesCacheKey, allModules, 24, TimeUnit.HOURS);
            }
        }
        
        // 获取所有API
        String allApisCacheKey = UserPermissionConstant.ALL_APIS;
        List<ApiModel> allApis;
        Object cachedApis = redisTemplate.opsForValue().get(allApisCacheKey);
        
        if (cachedApis != null) {
            log.info("[PermissionServiceImpl] getUserPermissions 从缓存获取所有API");
            allApis = JSON.parseArray(JSON.toJSONString(cachedApis), ApiModel.class);
        } else {
            log.info("[PermissionServiceImpl] getUserPermissions 从数据库获取所有API");
            allApis = apiMapper.getAllApis();
            // 缓存所有API（24小时过期，因为API变动较少）
            if (!allApis.isEmpty()) {
                redisTemplate.opsForValue().set(allApisCacheKey, allApis, 24, TimeUnit.HOURS);
            }
        }
        
        // 创建API ID到API对象的映射，方便后续查找
        Map<String, ApiModel> apiIdToModelMap = allApis.stream()
                .collect(Collectors.toMap(ApiModel::getApiId, api -> api));
        
        // 3. 获取所有角色的API权限
        Map<String, List<String>> roleApiMap = new HashMap<>();
        Map<String, List<ApiDTO>> roleApiDetailsMap = new HashMap<>();
        
        for (RoleDTO role : roles) {
            // 尝试从缓存获取角色API权限
            String roleApisCacheKey = UserPermissionConstant.ROLE_PERMISSIONS + ":" + role.getRoleId();
            Object cachedRoleApis = redisTemplate.opsForValue().get(roleApisCacheKey);
            
            // 尝试从缓存获取角色API详情
            String roleApiDetailsCacheKey = UserPermissionConstant.ROLE_API_DETAILS + ":" + role.getRoleId();
            Object cachedRoleApiDetails = redisTemplate.opsForValue().get(roleApiDetailsCacheKey);
            
            List<String> roleApiIds;
            List<ApiDTO> roleApiDetails;
            
            if (cachedRoleApis != null) {
                log.info("[PermissionServiceImpl] getUserPermissions 从缓存获取角色API权限: {}", role.getRoleId());
                roleApiIds = JSON.parseArray(JSON.toJSONString(cachedRoleApis), String.class);
                roleApiMap.put(role.getRoleId(), roleApiIds);
                
                if (cachedRoleApiDetails != null) {
                    log.info("[PermissionServiceImpl] getUserPermissions 从缓存获取角色API详情: {}", role.getRoleId());
                    roleApiDetails = JSON.parseArray(JSON.toJSONString(cachedRoleApiDetails), ApiDTO.class);
                    roleApiDetailsMap.put(role.getRoleId(), roleApiDetails);
                } else {
                    // 如果有API ID缓存但没有API详情缓存，则根据ID构建详情
                    log.info("[PermissionServiceImpl] getUserPermissions 根据缓存的API ID构建API详情: {}", role.getRoleId());
                    roleApiDetails = roleApiIds.stream()
                            .filter(apiIdToModelMap::containsKey)
                            .map(apiId -> convertToApiDTO(apiIdToModelMap.get(apiId)))
                            .collect(Collectors.toList());
                    
                    // 缓存角色API详情（2小时过期）
                    if (!roleApiDetails.isEmpty()) {
                        redisTemplate.opsForValue().set(roleApiDetailsCacheKey, roleApiDetails, 2, TimeUnit.HOURS);
                    }
                    roleApiDetailsMap.put(role.getRoleId(), roleApiDetails);
                }
            } else {
                log.info("[PermissionServiceImpl] getUserPermissions 从数据库获取角色API权限: {}", role.getRoleId());
                roleApiIds = apiMapper.getRoleApiIds(role.getRoleId());
                roleApiMap.put(role.getRoleId(), roleApiIds);
                
                // 根据API ID构建API详情
                roleApiDetails = roleApiIds.stream()
                        .filter(apiIdToModelMap::containsKey)
                        .map(apiId -> convertToApiDTO(apiIdToModelMap.get(apiId)))
                        .collect(Collectors.toList());
                
                // 缓存角色API权限（2小时过期）
                if (!roleApiIds.isEmpty()) {
                    redisTemplate.opsForValue().set(roleApisCacheKey, roleApiIds, 2, TimeUnit.HOURS);
                }
                
                // 缓存角色API详情（2小时过期）
                if (!roleApiDetails.isEmpty()) {
                    redisTemplate.opsForValue().set(roleApiDetailsCacheKey, roleApiDetails, 2, TimeUnit.HOURS);
                }
                roleApiDetailsMap.put(role.getRoleId(), roleApiDetails);
            }
        }
        
        // 4. 按模块分组所有API
        Map<String, List<ApiModel>> moduleApiMap = allApis.stream()
                .collect(Collectors.groupingBy(ApiModel::getModuleId));
        
        // 5. 构建用户权限
        List<ModuleDTO> userModules = new ArrayList<>();
        Map<String, List<ApiDTO>> userModuleApis = new HashMap<>();
        
        // 6. 遍历所有模块，确定用户有权限的模块和API
        for (ModuleModel module : allModules) {
            List<ApiModel> moduleApis = moduleApiMap.getOrDefault(module.getModuleId(), new ArrayList<>());
            
            // 检查用户是否有该模块下的任何API权限
            boolean hasModulePermission = false;
            List<ApiDTO> userApis = new ArrayList<>();
            
            for (ApiModel api : moduleApis) {
                // 检查用户的任一角色是否有该API权限
                boolean hasApiPermission = false;
                ApiDTO apiDTO = null;
                
                for (RoleDTO role : roles) {
                    List<String> roleApiIds = roleApiMap.get(role.getRoleId());
                    if (roleApiIds != null && roleApiIds.contains(api.getApiId())) {
                        hasApiPermission = true;
                        hasModulePermission = true;
                        
                        // 尝试从缓存的角色API详情中获取API DTO
                        List<ApiDTO> roleApiDetails = roleApiDetailsMap.get(role.getRoleId());
                        if (roleApiDetails != null) {
                            // 查找匹配的API DTO
                            apiDTO = roleApiDetails.stream()
                                    .filter(dto -> dto.getApiId().equals(api.getApiId()))
                                    .findFirst()
                                    .orElse(null);
                            
                            if (apiDTO != null) {
                                break; // 找到缓存的API DTO，跳出角色循环
                            }
                        }
                    }
                }
                
                if (hasApiPermission) {
                    // 如果在缓存中没有找到API DTO，则转换一个
                    if (apiDTO == null) {
                        apiDTO = convertToApiDTO(api);
                    }
                    userApis.add(apiDTO);
                }
            }
            
            // 如果用户有该模块的权限，添加到结果中
            if (hasModulePermission) {
                ModuleDTO moduleDTO = convertToModuleDTO(module);
                userModules.add(moduleDTO);
                userModuleApis.put(module.getModuleId(), userApis);
            }
        }
        
        // 7. 构建并返回权限响应
        UserPermissionResp result = UserPermissionResp.builder()
                .userId(userId)
                .roles(roles)
                .modules(userModules)
                .moduleApis(userModuleApis)
                .build();
        
        log.info("[PermissionServiceImpl] getUserPermissions 用户权限获取完成: {}, 角色数: {}, 模块数: {}", 
                userId, roles.size(), userModules.size());
        
        return result;
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