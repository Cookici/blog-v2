package com.lrh.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.lrh.gateway.client.PermissionClient;
import com.lrh.gateway.client.dto.ApiDTO;
import com.lrh.gateway.client.dto.ModuleDTO;
import com.lrh.gateway.client.dto.UserPermissionResp;
import com.lrh.gateway.constant.PasswordKeyConstant;
import com.lrh.gateway.constant.UserPermissionConstant;
import com.lrh.gateway.constant.WhiteListConstant;
import com.lrh.gateway.result.Result;
import com.lrh.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.gateway.filter
 * @ClassName: GlobeFilter
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 15:21
 */
@Slf4j
@Component
public class CustomGlobeFilter implements GlobalFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PermissionClient permissionClient;

    public CustomGlobeFilter(RedisTemplate<String, Object> redisTemplate, PermissionClient permissionClient) {
        this.redisTemplate = redisTemplate;
        this.permissionClient = permissionClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();
        String method = Objects.requireNonNull(exchange.getRequest().getMethod()).name();

        // 白名单直接放行
        for (String whiteUrl : WhiteListConstant.WHITE_LIST) {
            if (requestPath.contains(whiteUrl)) {
                return chain.filter(exchange);
            }
        }

        String userId = exchange.getRequest().getHeaders().getFirst(PasswordKeyConstant.HEADER_USER_ID);

        // 未登录用户处理
        if (userId == null) {
            return isAnonymousAccessible(requestPath, method)
                    .flatMap(accessible ->
                            accessible ? chain.filter(exchange) : unauthorized(exchange));
        }

        // 验证token
        String token = (String) redisTemplate.opsForHash().get(PasswordKeyConstant.LOGIN_HASH_KEY, userId);
        if (token == null) {
            return unauthorized(exchange);
        }

        try {
            JwtUtil.verify(token);
            exchange.getRequest().mutate()
                    .header(PasswordKeyConstant.Authorization, token)
                    .build();
        } catch (Exception e) {
            log.error("[CustomGlobeFilter] token verify error : {}", e.getMessage());
            redisTemplate.opsForHash().delete(PasswordKeyConstant.LOGIN_HASH_KEY, userId);
            return unauthorized(exchange);
        }

        // 验证权限
        return checkPermission(userId, requestPath, method)
                .flatMap(hasPermission ->
                        hasPermission ? chain.filter(exchange) : forbidden(exchange));
    }

    private Mono<Boolean> isAnonymousAccessible(String path, String method) {
        return Mono.fromCallable(() -> 
                redisTemplate.opsForHash().get(UserPermissionConstant.USER_PERMISSION, UserPermissionConstant.NO_LOGIN_USER_ID)
            )
            .flatMap(anonymousPermissions -> 
                Mono.just(checkApiPermission(
                    JSON.parseObject(JSON.toJSONString(anonymousPermissions), UserPermissionResp.class),
                    path, method))
            )
            .switchIfEmpty(
                Mono.fromCallable(() -> permissionClient.getUserPermissions(UserPermissionConstant.NO_LOGIN_USER_ID))
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(result -> {
                        if (result.getCode() != HttpStatus.OK.value()) {
                            return false;
                        }
                        UserPermissionResp permissions = result.getData();
                        return checkApiPermission(permissions, path, method);
                    })
            );
    }

    private Mono<Boolean> checkPermission(String userId, String path, String method) {
        return Mono.fromCallable(() -> 
                redisTemplate.opsForHash().get(UserPermissionConstant.USER_PERMISSION, userId)
            )
            .flatMap(userPermissions -> 
                Mono.just(checkApiPermission(
                    JSON.parseObject(JSON.toJSONString(userPermissions), UserPermissionResp.class),
                    path, method))
            )
            .switchIfEmpty(
                Mono.fromCallable(() -> permissionClient.getUserPermissions(userId))
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(result -> {
                        if (result.getCode() != HttpStatus.OK.value()) {
                            return false;
                        }
                        UserPermissionResp permissions = result.getData();
                        return checkApiPermission(permissions, path, method);
                    })
            );
    }

    private boolean checkApiPermission(UserPermissionResp permissions, String path, String method) {
        // 首先检查permissions对象是否为空
        if (permissions == null) {
            return false;
        }
        
        // 检查modules列表是否为空
        List<ModuleDTO> modules = permissions.getModules();
        if (modules == null || modules.isEmpty()) {
            return false;
        }
        
        // 检查moduleApis映射是否为空
        Map<String, List<ApiDTO>> moduleApis = permissions.getModuleApis();
        if (moduleApis == null || moduleApis.isEmpty()) {
            return false;
        }
        
        // 遍历模块
        return modules.stream().anyMatch(module -> {
            // 检查模块是否为空或模块前缀是否为空
            if (module == null || module.getModulePrefix() == null) {
                return false;
            }
            
            // 检查是否匹配模块前缀
            if (path.startsWith(module.getModulePrefix())) {
                // 获取该模块的API列表
                String moduleId = module.getModuleId();
                if (moduleId == null) {
                    return false;
                }
                
                List<ApiDTO> apis = moduleApis.get(moduleId);
                if (apis == null || apis.isEmpty()) {
                    return false;
                }
                
                // 检查是否有匹配的API权限
                return apis.stream().anyMatch(api ->
                        api != null && 
                        api.getApiPath() != null && 
                        api.getApiMethod() != null &&
                        path.equals(api.getApiPath()) && 
                        method.equals(api.getApiMethod())
                );
            }
            return false;
        });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        return writeResponse(exchange, HttpStatus.UNAUTHORIZED, "未授权访问");
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        return writeResponse(exchange, HttpStatus.FORBIDDEN, "无访问权限");
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        Result<Object> result = Result.fail()
                .code(status.value())
                .message(message);

        String responseBody = JSON.toJSONString(result);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);

        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
