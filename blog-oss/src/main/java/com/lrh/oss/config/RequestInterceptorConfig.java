package com.lrh.oss.config;

import com.lrh.common.openfeign.CustomFeignRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.config
 * @ClassName: RequestInterceptorConfig
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 16:55
 */
@Configuration
public class RequestInterceptorConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new CustomFeignRequestInterceptor();
    }


}
