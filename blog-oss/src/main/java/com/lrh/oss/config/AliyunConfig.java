package com.lrh.oss.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope  // 添加这个注解，确保配置可以被动态刷新
@Data
public class AliyunConfig {

    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.bucketName}")
    private String bucketName;

    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    @Bean
    public OSS ossClient() {
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            throw new IllegalArgumentException("Access Key ID is missing");
        }
        if (accessKeySecret == null || accessKeySecret.isEmpty()) {
            throw new IllegalArgumentException("Access Key Secret is missing");
        }
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}