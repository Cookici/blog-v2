package com.lrh.article.domain.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ThreadPoolService {
    private final ThreadPoolExecutor threadPoolExecutor;


    public ThreadPoolService(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void submitTask(Runnable task) {
        threadPoolExecutor.submit(task);
    }


    public void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
