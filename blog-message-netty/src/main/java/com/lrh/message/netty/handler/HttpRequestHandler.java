package com.lrh.message.netty.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrh.common.util.JwtUtil;
import com.lrh.message.config.NettyConfig;
import com.lrh.message.constants.AuthorizationConstant;
import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.netty.Attributes;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.utils.NettyUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.netty.handler
 * @ClassName: HttpHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 19:57
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Value("${netty.port}")
    private Integer nettyPort;

    private final RedisTemplate<String, Object> redisTemplate;

    public HttpRequestHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest msg) throws Exception {
        String token = extractTokenFromCookie(msg);
        if (token == null) {
            log.info("[HttpRequestHandler] error token is null");
            throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        String userId;
        try {
            DecodedJWT verify = JwtUtil.verify(token);
            userId = verify.getClaim("userId").asString();
        } catch (Exception e) {
            log.info("[HttpRequestHandler] error : {}", e.getMessage());
            throw new RuntimeException(e);
        }

        if (userId == null || userId.isEmpty()) {
            log.info("[HttpRequestHandler] error userId is null");
            throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        NettyConfig.group.add(channelHandlerContext.channel());
        channelHandlerContext.channel().attr(Attributes.USERID).set(userId);
        ChannelContext.addChannel(userId, channelHandlerContext.channel());
        redisTemplate.opsForHash().put(RedisKeyConstant.USERID_NETTY_HASH_KEY, userId,
                NettyUtil.getLocalHostExactAddress().getHostAddress() + ":" + nettyPort);
        log.info("[WebSocketServer] 连接:{}", userId);
        channelHandlerContext.pipeline().remove(this);
        channelHandlerContext.fireChannelRead(msg.retain());
    }


    /**
     * 从HTTP请求的Cookie中提取Authorization令牌
     * @param request HTTP请求
     * @return Authorization令牌，如果不存在则返回null
     */
    private String extractTokenFromCookie(FullHttpRequest request) {
        String cookieHeader = request.headers().get("Cookie");
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }
        
        // 解析Cookie字符串
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && AuthorizationConstant.AUTHORIZATION.equals(parts[0])) {
                return parts[1];
            }
        }
        
        // 如果没有在Cookie中找到，尝试从Authorization头获取
        String authHeader = request.headers().get(AuthorizationConstant.AUTHORIZATION);
        if (authHeader != null && !authHeader.isEmpty()) {
            // 如果Authorization头以"Bearer "开头，则去掉前缀
            if (authHeader.startsWith(AuthorizationConstant.AUTHORIZATION_TYPE)) {
                return authHeader.substring(7);
            }
            return authHeader;
        }
        
        return null;
    }
}
