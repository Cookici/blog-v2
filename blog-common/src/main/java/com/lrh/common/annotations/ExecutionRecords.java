package com.lrh.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName:    blog-ddd 
 * @Package:        com.lrh.common.enums
 * @ClassName:      ExecutionRecordsEnum
 * @Author:     63283
 * @Description:    
 * @Date:    2024/12/26 01:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionRecords {
    String key(); // 方法的特定key
    String userLabel(); // 用户标识
    int maxTimes(); // 最大操作次数
    int cooldown(); // 冷却时间
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 时间单位
}
