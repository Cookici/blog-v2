package com.lrh.oss.aspect;

import com.lrh.common.annotations.SubmitOnceRecords;
import com.lrh.common.constant.BusinessConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.common.aspect
 * @ClassName: SubmitOnceAspect
 * @Author: 63283
 * @Description: AOP aspect to prevent duplicate submissions.
 * @Date: 2024/12/26 01:36
 */

@Aspect
@Component
public class SubmitOnceAspect {

    private final RedissonClient redissonClient;

    public SubmitOnceAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(submitOnceRecords)")
    public Object controlExecution(ProceedingJoinPoint joinPoint, SubmitOnceRecords submitOnceRecords) throws Throwable {
        // 解析注解中的 key 和 userLabel 表达式
        String key = submitOnceRecords.key();
        String userExpression = submitOnceRecords.userLabel();

        // 使用 SpEL 解析注解中的动态参数
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String resolvedUserLabel;
        try {
            resolvedUserLabel = parser.parseExpression(userExpression).getValue(context, String.class);
        } catch (Exception e) {
            throw new RuntimeException("SpEL 表达式解析失败，请检查: " + userExpression);
        }

        // 拼接 Redis Key
        String redisKey = String.format(BusinessConstant.SUBMIT_ONCE_UPLOAD_LOCK, key, resolvedUserLabel);

        // 获取 Redisson 分布式锁
        RLock lock = redissonClient.getLock(redisKey);
        if (lock.tryLock()) {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("操作正在处理中，请稍后重试！");
        }
    }
}
