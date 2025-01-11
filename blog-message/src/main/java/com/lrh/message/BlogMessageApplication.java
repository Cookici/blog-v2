package com.lrh.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author 63283
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableWebSocket
public class BlogMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogMessageApplication.class, args);
    }

}
