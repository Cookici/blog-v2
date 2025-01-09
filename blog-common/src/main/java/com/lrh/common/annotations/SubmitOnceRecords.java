package com.lrh.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubmitOnceRecords {
    String key(); // 方法的特定key

    String userLabel(); // 用户标识

    int expireTime() default 30; // redis锁的过期时间，默认30s
}
