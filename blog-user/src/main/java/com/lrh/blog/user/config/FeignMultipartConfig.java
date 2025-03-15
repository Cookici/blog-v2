package com.lrh.blog.user.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignMultipartConfig {
    
    private final ObjectFactory<HttpMessageConverters> messageConverters;
    
    public FeignMultipartConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }
    
    @Bean
    @Primary
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
