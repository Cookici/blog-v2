package com.lrh.article.infrastructure.config;

import com.lrh.common.context.filter.UserInfoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.config
 * @ClassName: FilterConfigi
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 16:56
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<UserInfoFilter> filterRegistrationBean(){
        FilterRegistrationBean<UserInfoFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new UserInfoFilter());
        bean.addUrlPatterns("/*");
        return bean;
    }

}
