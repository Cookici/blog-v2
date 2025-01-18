package com.lrh.message.config;

import com.lrh.message.designpattern.builder.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.config
 * @ClassName: ThreadPoolConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 15:12
 */

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolBuilder()
                .corePoolSize(4)
                .maximumPoolSize(8)
                .keepAliveTime(30L, TimeUnit.SECONDS)
                .workQueue(new LinkedBlockingQueue<>(4096))
                .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory("message-worker", true)
                .build();
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
