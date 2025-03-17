package com.lrh.article.infrastructure.config;

import com.lrh.article.infrastructure.designpattern.builder.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
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
        return new ThreadPoolBuilder()
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTime(60L, TimeUnit.SECONDS)
                .workQueue(new LinkedBlockingQueue<>(1000))
                .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory("task-executor", true)
                .build();
    }

    @Bean(name = "articleAsyncExecutor")
    public Executor articleAsyncExecutor() {
        return new ThreadPoolBuilder()
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTime(60L, TimeUnit.SECONDS)
                .workQueue(new LinkedBlockingQueue<>(100))
                .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory("article-async", true)
                .build();
    }
}
