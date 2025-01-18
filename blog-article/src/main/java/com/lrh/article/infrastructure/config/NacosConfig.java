package com.lrh.article.infrastructure.config;

import com.lrh.common.nacos.NacosBeanAutowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.config
 * @ClassName: NacosConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/18 17:03
 */
@Configuration
public class NacosConfig {

    @Bean
    public NacosBeanAutowire nacosBeanAutowire() {
        return new NacosBeanAutowire();
    }

}
