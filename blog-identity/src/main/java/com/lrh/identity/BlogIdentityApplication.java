package com.lrh.identity;

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
public class BlogIdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogIdentityApplication.class, args);
    }

}
