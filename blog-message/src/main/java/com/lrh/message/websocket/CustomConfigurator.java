package com.lrh.message.websocket;

import com.lrh.common.constant.PasswordKeyConstant;
import com.lrh.common.util.JwtUtil;
import com.lrh.message.constants.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.websocket
 * @ClassName: CustomConfigurator
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/9 23:15
 */
@Slf4j
@Configuration
public class CustomConfigurator extends ServerEndpointConfig.Configurator {

    private static RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public void setAbstractStrategyChoose(RedisTemplate<String,Object> redisTemplate) {
        CustomConfigurator.redisTemplate = redisTemplate;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        List<String> userIdHeaders = request.getHeaders().get(PasswordKeyConstant.HEADER_USER_ID);
        String userId = (userIdHeaders != null && !userIdHeaders.isEmpty()) ? userIdHeaders.get(0) : null;
        if(userId == null){
            return;
        }
        try {
            verify(userId);
        } catch (Exception e) {
            log.error("[CustomConfigurator] modifyToken error: {}", e.getMessage());
            return;
        }
        sec.getUserProperties().put(PasswordKeyConstant.HEADER_USER_ID, userId);
    }

    private void verify(String userId){
        String token = (String) redisTemplate.opsForHash().get(RedisKeyConstant.LOGIN_HASH_KEY, userId);
        JwtUtil.verify(token);
    }
}
