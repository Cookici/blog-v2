package com.lrh.identity.util;

import com.lrh.identity.constants.RoleConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 权限服务分布式锁工具类
 * 用于处理权限服务中的并发问题
 */
@Slf4j
@Component
public class LockUtil {

    private final RedissonClient redissonClient;

    public LockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取角色权限锁的键
     * @param roleId 角色ID
     * @return 锁键
     */
    public String getRolePermissionLockKey(String roleId) {
        return RoleConstant.ROLE_PERMISSION_LOCK + roleId;
    }


    /**
     * 使用双重检查锁模式执行有返回值的任务
     * 先检查缓存，如果缓存未命中，则获取锁并再次检查缓存
     * @param lockKey 锁键
     * @param cacheCheckFunc 缓存检查函数
     * @param dbFallbackFunc 数据库回退函数
     * @param <T> 返回值类型
     * @return 任务执行结果
     */
    public <T> T executeWithDoubleCheck(String lockKey, Supplier<T> cacheCheckFunc, Supplier<T> dbFallbackFunc) {
        // 第一次检查缓存
        T result = cacheCheckFunc.get();
        if (result != null) {
            return result;
        }
        
        // 缓存未命中，获取锁
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            log.debug("[LockUtil] 获取锁成功(双重检查): {}", lockKey);
            
            // 第二次检查缓存
            result = cacheCheckFunc.get();
            if (result != null) {
                return result;
            }
            
            // 缓存仍未命中，从数据库获取
            return dbFallbackFunc.get();
        } catch (Exception e) {
            log.error("[LockUtil] 执行任务出错(双重检查) [{}]: {}", lockKey, e.getMessage(), e);
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("[LockUtil] 释放锁(双重检查): {}", lockKey);
            }
        }
    }
}