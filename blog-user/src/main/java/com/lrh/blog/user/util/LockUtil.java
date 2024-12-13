package com.lrh.blog.user.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.util
 * @ClassName: LockUtil
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 23:26
 */
@Slf4j
public class LockUtil {

    private final RedissonClient redissonClient;

    public LockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * Executes a task with a distributed lock.
     *
     * @param lockKey   The key for the lock.
     * @param leaseTime The lock lease time.
     * @param unit      The time unit for the lease time.
     * @param task      The task to execute.
     * @param <T>       The return type of the task.
     * @return The result of the task execution.
     */
    public <T> T executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
        try {
            return task.get();
        } catch (Exception e) {
            log.error("Error while executing task with lock [{}]: {}", lockKey, e.getMessage(), e);
            throw new RuntimeException("Task execution failed", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Executes a task with a distributed lock without returning a result.
     *
     * @param lockKey   The key for the lock.
     * @param leaseTime The lock lease time.
     * @param unit      The time unit for the lease time.
     * @param task      The task to execute.
     */
    public void executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
        try {
            task.run();
        } catch (Exception e) {
            log.error("Error while executing task with lock [{}]: {}", lockKey, e.getMessage(), e);
            throw new RuntimeException("Task execution failed", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

