package com.lrh.oss.config;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@Data
public class BaiDuTextSensingConfig {

    @Value("${baidu.API_KEY}")
    private String apiKey;

    @Value("${baidu.SECRET_KEY}")
    private String secretKey;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }
}
