package com.lrh.identity.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lrh.identity.constants.RoleConstant;
import com.lrh.identity.dto.ApiDTO;
import com.lrh.identity.dto.ModuleDTO;
import com.lrh.identity.dto.req.UserRoleBindReq;
import com.lrh.identity.dto.resp.ModuleApisDTO;
import com.lrh.identity.mapper.ApiMapper;
import com.lrh.identity.mapper.ModuleMapper;
import com.lrh.identity.mapper.RoleMapper;
import com.lrh.identity.model.ApiModel;
import com.lrh.identity.model.ModuleModel;
import com.lrh.identity.service.RoleService;
import com.lrh.identity.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final ModuleMapper moduleMapper;
    private final ApiMapper apiMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LockUtil lockUtil;

    public RoleServiceImpl(RoleMapper roleMapper, ModuleMapper moduleMapper, ApiMapper apiMapper,
                           RedisTemplate<String, Object> redisTemplate, LockUtil lockUtil) {
        this.roleMapper = roleMapper;
        this.moduleMapper = moduleMapper;
        this.apiMapper = apiMapper;
        this.redisTemplate = redisTemplate;
        this.lockUtil = lockUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindUserRole(UserRoleBindReq req) {
        log.info("[RoleServiceImpl] bindUserRole 开始绑定用户角色: {}", JSON.toJSONString(req));
        Integer result = roleMapper.insertUserRole(req.getUserId(), req.getRoleId());
        return result > 0;
    }

    @Override
    public String getRoleIdByUserId(String userId) {
        log.info("[RoleServiceImpl] getRoleIdByUserId 开始获取用户角色ID: {}", userId);
        String roleId = roleMapper.getUserRoleId(userId);
        if(roleId == null || roleId.isEmpty()) {
            return RoleConstant.NO_LOGIN_ROLE;
        }
        return roleId;
    }

    @Override
    public List<ModuleApisDTO> getRoleModuleApis(String roleId) {
        log.info("[RoleServiceImpl] getRoleModuleApis 开始获取角色模块API: {}", roleId);

        if (roleId == null || roleId.isEmpty()) {
            return Collections.emptyList();
        }

        // 尝试从缓存获取角色模块API
        String roleModuleApisCacheKey = RoleConstant.ROLE_MODULE_APIS + ":" + roleId;
        Object cachedRoleModuleApis = redisTemplate.opsForValue().get(roleModuleApisCacheKey);

        List<ModuleApisDTO> roleModuleApis;
        if (cachedRoleModuleApis != null) {
            log.info("[RoleServiceImpl] getRoleModuleApis 从缓存获取角色模块API: {}", roleId);
            roleModuleApis = JSON.parseArray(JSON.toJSONString(cachedRoleModuleApis), ModuleApisDTO.class);
        } else {
            // 使用分布式锁获取角色模块API
            roleModuleApis = lockUtil.executeWithDoubleCheck(
                    lockUtil.getRolePermissionLockKey(roleId),
                    // 缓存检查函数
                    () -> {
                        Object cached = redisTemplate.opsForValue().get(roleModuleApisCacheKey);
                        if (cached != null) {
                            return JSON.parseArray(JSON.toJSONString(cached), ModuleApisDTO.class);
                        }
                        return null;
                    },
                    // 数据库回退函数
                    () -> fetchRoleModuleApisFromDB(roleId, roleModuleApisCacheKey)
            );
        }

        return roleModuleApis != null ? roleModuleApis : Collections.emptyList();
    }
    
    /**
     * 从数据库获取角色模块API
     */
    private List<ModuleApisDTO> fetchRoleModuleApisFromDB(String roleId, String cacheKey) {
        log.info("[RoleServiceImpl] fetchRoleModuleApisFromDB 从数据库获取角色模块API: {}", roleId);
        
        // 获取角色API权限
        List<String> roleApiIds = apiMapper.getRoleApiIds(roleId);
        if (CollectionUtils.isEmpty(roleApiIds)) {
            return Collections.emptyList();
        }

        // 获取所有API信息
        List<ApiModel> allApis = apiMapper.getAllApis();
        if (CollectionUtils.isEmpty(allApis)) {
            return Collections.emptyList();
        }

        // 过滤出角色拥有权限的API
        List<ApiModel> roleApis = allApis.stream()
                .filter(api -> roleApiIds.contains(api.getApiId()))
                .collect(Collectors.toList());

        if (roleApis.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取API对应的模块ID
        Set<String> moduleIds = roleApis.stream()
                .map(ApiModel::getModuleId)
                .collect(Collectors.toSet());

        // 获取所有模块信息
        List<ModuleModel> allModules = moduleMapper.getAllModules();
        if (CollectionUtils.isEmpty(allModules)) {
            return Collections.emptyList();
        }

        // 过滤出角色拥有权限的模块
        List<ModuleModel> roleModules = allModules.stream()
                .filter(module -> moduleIds.contains(module.getModuleId()))
                .collect(Collectors.toList());

        // 按模块分组API
        Map<String, List<ApiModel>> moduleApiMap = roleApis.stream()
                .collect(Collectors.groupingBy(ApiModel::getModuleId));

        // 构建角色模块API
        List<ModuleApisDTO> moduleApisList = buildModuleApisList(roleModules, moduleApiMap);

        // 缓存角色模块API（2小时过期）
        if (!moduleApisList.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, moduleApisList, 2, TimeUnit.HOURS);
        }

        return moduleApisList;
    }
    
    /**
     * 构建模块API列表
     */
    private List<ModuleApisDTO> buildModuleApisList(List<ModuleModel> modules, Map<String, List<ApiModel>> moduleApiMap) {
        List<ModuleApisDTO> result = new ArrayList<>();
        
        for (ModuleModel module : modules) {
            ModuleApisDTO moduleApisDTO = new ModuleApisDTO();
            
            // 设置模块信息
            List<ModuleDTO> moduleDTOs = new ArrayList<>();
            moduleDTOs.add(convertToModuleDTO(module));
            moduleApisDTO.setModules(moduleDTOs);
            
            // 设置模块对应的API
            Map<String, ApiDTO> apiDTOMap = new HashMap<>();
            List<ApiModel> moduleApis = moduleApiMap.getOrDefault(module.getModuleId(), Collections.emptyList());
            
            for (ApiModel api : moduleApis) {
                apiDTOMap.put(api.getApiId(), convertToApiDTO(api));
            }
            
            moduleApisDTO.setModuleApis(apiDTOMap);
            result.add(moduleApisDTO);
        }
        
        return result;
    }

    /**
     * 将ModuleModel转换为ModuleDTO
     */
    private ModuleDTO convertToModuleDTO(ModuleModel module) {
        return ModuleDTO.builder()
                .moduleId(module.getModuleId())
                .moduleName(module.getModuleName())
                .modulePrefix(module.getModulePrefix())
                .moduleDesc(module.getModuleDesc())
                .parentId(module.getParentId())
                .build();
    }

    /**
     * 将ApiModel转换为ApiDTO
     */
    private ApiDTO convertToApiDTO(ApiModel api) {
        return ApiDTO.builder()
                .apiId(api.getApiId())
                .moduleId(api.getModuleId())
                .apiName(api.getApiName())
                .apiPath(api.getApiPath())
                .apiMethod(api.getApiMethod())
                .apiDesc(api.getApiDesc())
                .build();
    }
}
