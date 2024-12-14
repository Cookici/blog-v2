package com.lrh.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 63283
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BlogArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogArticleApplication.class, args);
    }

}