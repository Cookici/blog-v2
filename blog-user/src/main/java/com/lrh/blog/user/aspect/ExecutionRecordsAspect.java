package com.lrh.blog.user.aspect;

import com.lrh.common.annotations.ExecutionRecords;
import com.lrh.common.constant.BusinessConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.common.aspect
 * @ClassName: ExecutionRecordsAspect
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/26 01:36
 */

@Aspect
@Component
public class ExecutionRecordsAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    public ExecutionRecordsAspect(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(executionRecords)")
    public Object controlExecution(ProceedingJoinPoint joinPoint, ExecutionRecords executionRecords) throws Throwable {
        String key = executionRecords.key();
        String userExpression = executionRecords.userLabel();
        int maxTimes = executionRecords.maxTimes();
        int cooldown = executionRecords.cooldown();
        TimeUnit timeUnit = executionRecords.timeUnit();

        // Step 1: 获取方法参数及名称
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        // Step 2: 创建 SpEL 解析器和上下文
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            // 绑定参数到上下文
            context.setVariable(paramNames[i], args[i]);
        }

        // Step 3: 解析 SpEL 表达式
        String resolvedUserLabel;
        try {
            resolvedUserLabel = parser.parseExpression(userExpression).getValue(context, String.class);
        } catch (Exception e) {
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }

        RLock lock = redissonClient.getLock(String.format(BusinessConstant.EXECUTION_RECORD_LOCK, key));
        if (lock.tryLock()) {
            try {
                String redisKey = String.format(BusinessConstant.EXECUTION_RECORD_KEY, key, resolvedUserLabel);
                Integer currentCount = (Integer) redisTemplate.opsForValue().get(redisKey);
                if (currentCount != null && currentCount >= maxTimes) {
                    throw new RuntimeException("达到最大操作次数，请稍后再试");
                }
                try {
                    Object result = joinPoint.proceed();
                    redisTemplate.delete(redisKey);
                    return result;
                } catch (Throwable ex) {
                    // 增加计数器
                    if (currentCount == null) {
                        redisTemplate.opsForValue().set(redisKey, 1, cooldown, timeUnit);
                    } else {
                        redisTemplate.opsForValue().increment(redisKey);
                    }
                    // 向上抛出原始异常
                    throw ex;
                }
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException(BusinessConstant.OPERATOR_MUCH);
        }
    }

}
