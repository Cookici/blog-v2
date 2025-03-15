package com.lrh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 63283
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class BlogGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogGatewayApplication.class, args);
    }

}
