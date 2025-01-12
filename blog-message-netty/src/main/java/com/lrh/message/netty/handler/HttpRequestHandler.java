package com.lrh.message.netty.handler;

import com.lrh.common.constant.PasswordKeyConstant;
import com.lrh.common.util.JwtUtil;
import com.lrh.message.config.NettyConfig;
import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.netty.Attributes;
import com.lrh.message.netty.ChannelContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
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
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    public HttpRequestHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            // 获取 HTTP 请求头信息
            String userId = httpRequest.headers().get(PasswordKeyConstant.HEADER_USER_ID);
            if (userId == null) {
                log.info("[HttpRequestHandler] error userId is null");
                throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            }
            String token = (String) redisTemplate.opsForHash().get(RedisKeyConstant.LOGIN_HASH_KEY, userId);
            if (token == null) {
                log.info("[HttpRequestHandler] error token is null");
                throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            }
            try {
                JwtUtil.verify(token);
            } catch (Exception e) {
                log.info("[HttpRequestHandler] error : {}", e.getMessage());
                throw new RuntimeException(e);
            }
            channelHandlerContext.channel().attr(Attributes.USERID).set(userId);
            ChannelContext.addChannel(userId, channelHandlerContext.channel());
            NettyConfig.group.add(channelHandlerContext.channel());
            log.info("[WebSocketServer] 连接:{}", userId);
            channelHandlerContext.pipeline().remove(this);
            channelHandlerContext.fireChannelRead(msg);
        }
    }
}
