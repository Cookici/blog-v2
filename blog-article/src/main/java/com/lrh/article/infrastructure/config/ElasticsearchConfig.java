package com.lrh.article.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${spring.elasticsearch.port}")
    private int elasticsearchPort;

    @Value("${spring.elasticsearch.username}")
    private String elasticsearchUsername;  // 添加用户名配置

    @Value("${spring.elasticsearch.password}")
    private String elasticsearchPassword;  // 添加密码配置

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        // 使用 builder() 来配置
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticsearchHost + ":" + elasticsearchPort)  // Elasticsearch 地址
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)  // 配置基本认证
                .build();  // 构建配置

        return new ElasticsearchRestTemplate(RestClients.create(clientConfiguration).rest());
    }
}