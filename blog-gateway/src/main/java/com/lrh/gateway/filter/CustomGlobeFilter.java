package com.lrh.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrh.gateway.client.RoleClient;
import com.lrh.gateway.client.dto.ApiDTO;
import com.lrh.gateway.client.dto.ModuleApisDTO;
import com.lrh.gateway.client.dto.ModuleDTO;
import com.lrh.gateway.constant.PasswordKeyConstant;
import com.lrh.gateway.constant.UserPermissionConstant;
import com.lrh.gateway.result.Result;
import com.lrh.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
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

    private final RoleClient roleClient;

    public CustomGlobeFilter(RoleClient roleClient) {
        this.roleClient = roleClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();
        String method = Objects.requireNonNull(exchange.getRequest().getMethod()).name();
        String token = exchange.getRequest().getHeaders().getFirst(PasswordKeyConstant.AUTHORIZATION);

        if (token == null || token.isEmpty()) {
            return isAnonymousAccessible(requestPath, method)
                    .flatMap(accessible ->
                            accessible ? chain.filter(exchange) : unauthorized(exchange));
        }


        String userId;
        String role;
        try {
            token = getToken(token);
            DecodedJWT verify = JwtUtil.verify(token);
            userId = verify.getClaim("userId").asString();
            role = verify.getClaim("role").asString();
            exchange.getRequest().mutate()
                    .header(PasswordKeyConstant.AUTHORIZATION, token)
                    .build();
        } catch (Exception e) {
            log.error("[CustomGlobeFilter] token verify error : {}", e.getMessage());
            return unauthorized(exchange);
        }

        if(userId == null || userId.isEmpty()) {
            return unauthorized(exchange);
        }

        if(role == null || role.isEmpty()) {
            return unauthorized(exchange);
        }


        // 验证权限
        return checkPermission(role, requestPath, method)
                .flatMap(hasPermission ->
                        hasPermission ? chain.filter(exchange) : forbidden(exchange));
    }

    private Mono<Boolean> isAnonymousAccessible(String path, String method) {
        return Mono.fromCallable(() -> roleClient.getRoleApis(UserPermissionConstant.NO_LOGIN_ROLE))
                .subscribeOn(Schedulers.boundedElastic())
                .map(result -> {
                    if (result.getCode() != HttpStatus.OK.value()) {
                        return false;
                    }
                    List<ModuleApisDTO> data = result.getData();
                    return checkApiPermission(data, path, method);
                });
    }

    private Mono<Boolean> checkPermission(String role, String path, String method) {
        return Mono.fromCallable(() -> roleClient.getRoleApis(role))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(result -> {
                            if (result.getCode() != HttpStatus.OK.value()) {
                                return false;
                            }
                            List<ModuleApisDTO> data = result.getData();
                            return checkApiPermission(data, path, method);
                        });
    }

    private boolean checkApiPermission(List<ModuleApisDTO> moduleApisList, String path, String method) {
        // 检查数据是否为空
        if (moduleApisList == null || moduleApisList.isEmpty()) {
            return false;
        }

        // 遍历所有模块
        for (ModuleApisDTO moduleApis : moduleApisList) {
            // 检查模块信息是否为空
            if (moduleApis == null || moduleApis.getModules() == null || moduleApis.getModules().isEmpty()) {
                continue;
            }

            // 获取模块信息
            ModuleDTO module = moduleApis.getModules().get(0);
            if (module == null || module.getModulePrefix() == null) {
                continue;
            }

            // 检查路径是否匹配模块前缀
            if (path.startsWith(module.getModulePrefix())) {
                // 检查模块API是否为空
                Map<String, ApiDTO> apiMap = moduleApis.getModuleApis();
                if (apiMap == null || apiMap.isEmpty()) {
                    continue;
                }

                // 遍历所有API检查是否有匹配的权限
                for (ApiDTO api : apiMap.values()) {
                    if (api != null &&
                            api.getApiPath() != null &&
                            api.getApiMethod() != null &&
                            matchesPath(path, api.getApiPath()) &&
                            method.equals(api.getApiMethod())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        return writeResponse(exchange, HttpStatus.UNAUTHORIZED, "未授权访问或登录已过期");
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

    private String getToken(String authorizationString) {
        if (authorizationString.startsWith(PasswordKeyConstant.AUTHORIZATION_TYPE)) {
            return authorizationString.substring(7);
        }
        return authorizationString;
    }

    /**
     * 匹配请求路径与API路径
     * 支持路径参数匹配，例如：/api/article/get/{articleId} 可以匹配 /api/article/get/article_88ff38930b5b4ad3b5c61fce603f3f3f
     * @param requestPath 请求路径
     * @param apiPath API路径定义
     * @return 是否匹配
     */
    private boolean matchesPath(String requestPath, String apiPath) {
        // 如果是精确匹配，直接返回
        if (requestPath.equals(apiPath)) {
            return true;
        }
        
        // 检查API路径是否包含路径参数 {xxx}
        if (!apiPath.contains("{")) {
            return false;
        }
        
        // 将API路径转换为正则表达式
        // 例如：/api/article/get/{articleId} -> /api/article/get/[^/]+
        String regexPath = apiPath.replaceAll("\\{[^/]+\\}", "[^/]+");
        
        // 使用正则表达式匹配
        return requestPath.matches(regexPath);
    }
}
