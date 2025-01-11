package com.lrh.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.config
 * @ClassName: WebSocketConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/9 22:30
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
