package com.lrh.message.config.designpattern;


import com.lrh.message.config.designpattern.chain.AbstractChainContext;
import com.lrh.message.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.message.config.spring.ApplicationBaseAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.config
 * @ClassName: DesignPatternAutoConfiguration
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 00:13
 */
@Configuration
@ImportAutoConfiguration(ApplicationBaseAutoConfiguration.class)
public class DesignPatternAutoConfiguration {
    /**
     * 策略模式选择器
     */
    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

    /**
     * 责任链上下文
     */
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }
}
