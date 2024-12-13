package com.lrh.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrh.gateway.constant.PasswordKeyConstant;
import com.lrh.gateway.constant.WhiteListConstant;
import com.lrh.gateway.context.UserInfoDTO;
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

import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    public CustomGlobeFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestPath = exchange.getRequest().getURI().getPath();

        for (String whiteUrl : WhiteListConstant.WHITE_LIST) {
            if (requestPath.contains(whiteUrl)) {
                return chain.filter(exchange);
            }
        }

        String userId = exchange.getRequest().getHeaders().getFirst(PasswordKeyConstant.HEADER_USER_ID);

        if(userId == null){
            DataBuffer dataBuffer = getFailDataBuffer(exchange);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        String token = (String) redisTemplate.opsForValue().get(userId);

        if (token == null) {
            DataBuffer dataBuffer = getFailDataBuffer(exchange);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        try {
            DecodedJWT verify = JwtUtil.verify(token);
            Map<String, Claim> claims = verify.getClaims();
            String userName = String.valueOf(claims.get("userName"));
            String roleName = String.valueOf(claims.get("roleName"));
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .userName(userName)
                    .roleName(roleName)
                    .token(token)
                    .build();
            exchange.getRequest().mutate()
                    .header(PasswordKeyConstant.Authorization, JSON.toJSONString(userInfoDTO))
                    .build();
        } catch (Exception e) {
            log.error("[CustomGlobeFilter] filter error : {}", e.getMessage());
            redisTemplate.opsForValue().getAndDelete(userId);
            DataBuffer dataBuffer = getFailDataBuffer(exchange);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        return chain.filter(exchange);
    }

    private static DataBuffer getFailDataBuffer(ServerWebExchange exchange) {
        // 创建自定义的 Result 对象
        Result<Object> message = Result.fail()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        // 将响应内容转为 JSON 字符串
        String responseBody = JSON.toJSONString(message);

        // 设置响应头为 JSON 类型
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 设置响应状态码为 401 未授权
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        // 将响应内容写入响应体
        return exchange.getResponse().bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
    }
}
