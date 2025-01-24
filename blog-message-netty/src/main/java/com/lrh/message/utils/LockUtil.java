package com.lrh.message.utils;

import com.lrh.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;

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
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
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
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Attempts to acquire a distributed lock within a specified waiting time.
     *
     * @param lockKey The key for the lock.
     * @param task    The task to execute if the lock is acquired.
     * @param <T>     The return type of the task.
     * @return The result of the task execution if the lock is acquired.
     * @throws RuntimeException If the lock cannot be acquired within the specified waiting time.
     */
    public <T> T tryLock(String lockKey, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                return task.get();
            } catch (Exception e) {
                log.error("Error while executing task with lock [{}]: {}", lockKey, e.getMessage(), e);
                throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException(BusinessConstant.OPERATOR_MUCH);
        }

    }

    /**
     * Attempts to acquire a distributed lock within a specified waiting time and executes a task if successful.
     *
     * @param lockKey The key for the lock..
     * @param task    The task to execute if the lock is acquired.
     * @throws RuntimeException If the lock cannot be acquired within the specified waiting time.
     */
    public void tryLock(String lockKey, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Error while executing task with lock [{}]: {}", lockKey, e.getMessage(), e);
                throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException(BusinessConstant.OPERATOR_MUCH);
        }
    }


    /**
     * Executes a read operation with a distributed read lock.
     *
     * @param lockKey   The key for the lock.
     * @param leaseTime The lock lease time.
     * @param unit      The time unit for the lease time.
     * @param task      The task to execute.
     * @param <T>       The return type of the task.
     * @return The result of the task execution.
     */
    public <T> T executeWithReadLock(String lockKey, long leaseTime, TimeUnit unit, Supplier<T> task) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = rwLock.readLock();
        readLock.lock(leaseTime, unit);
        try {
            return task.get();
        } catch (Exception e) {
            log.error("Error while executing read task with lock [{}]: {}", lockKey, e.getMessage(), e);
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        } finally {
            if (readLock.isLocked() && readLock.isHeldByCurrentThread()) {
                readLock.unlock();
            }
        }
    }

    /**
     * Executes a write operation with a distributed write lock.
     *
     * @param lockKey   The key for the lock.
     * @param leaseTime The lock lease time.
     * @param unit      The time unit for the lease time.
     * @param task      The task to execute.
     * @param <T>       The return type of the task.
     * @return The result of the task execution.
     */
    public <T> T executeWithWriteLock(String lockKey, long leaseTime, TimeUnit unit, Supplier<T> task) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = rwLock.writeLock();
        writeLock.lock(leaseTime, unit);
        try {
            return task.get();
        } catch (Exception e) {
            log.error("Error while executing write task with lock [{}]: {}", lockKey, e.getMessage(), e);
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        } finally {
            if (writeLock.isLocked() && writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
    }


    /**
     * Attempts to acquire a distributed read lock within a specified waiting time.
     *
     * @param lockKey The key for the lock.
     * @param task    The task to execute if the lock is acquired.
     * @param <T>     The return type of the task.
     * @return The result of the task execution if the lock is acquired.
     * @throws RuntimeException If the lock cannot be acquired within the specified waiting time.
     */
    public <T> T tryReadLock(String lockKey, Supplier<T> task) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = rwLock.readLock();
        if (readLock.tryLock()) {
            try {
                return task.get();
            } catch (Exception e) {
                log.error("Error while executing read task with lock [{}]: {}", lockKey, e.getMessage(), e);
                throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            } finally {
                if (readLock.isLocked() && readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
            }
        } else {
            throw new RuntimeException(BusinessConstant.OPERATOR_MUCH);
        }
    }

    /**
     * Attempts to acquire a distributed write lock within a specified waiting time.
     *
     * @param lockKey The key for the lock.
     * @param task    The task to execute if the lock is acquired.
     * @param <T>     The return type of the task.
     * @return The result of the task execution if the lock is acquired.
     * @throws RuntimeException If the lock cannot be acquired within the specified waiting time.
     */
    public <T> T tryWriteLock(String lockKey, Supplier<T> task) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = rwLock.writeLock();
        if (writeLock.tryLock()) {
            try {
                return task.get();
            } catch (Exception e) {
                log.error("Error while executing write task with lock [{}]: {}", lockKey, e.getMessage(), e);
                throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            } finally {
                if (writeLock.isLocked() && writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
        } else {
            throw new RuntimeException(BusinessConstant.OPERATOR_MUCH);
        }
    }



}


