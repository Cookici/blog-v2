package com.lrh.article.infrastructure.config.designpattern.strategy;

/**
 * @ProjectName: project-12306
 * @Package: com.lrh.project12306.framework.starter.designpattern.strategy
 * @ClassName: AbstractExecuteStrategy
 * @Author: 63283
 * @Description: 策略执行抽象
 * @Date: 2024/3/6 18:02
 */

public interface AbstractExecuteStrategy<REQUEST, RESPONSE> {
    /**
     * 执行策略标识
     */
    default String mark() {
        return null;
    }

    /**
     * 执行策略范匹配标识
     */
    default String patternMatchMark() {
        return null;
    }

    /**
     * 执行策略
     *
     * @param requestParam 执行策略入参
     */
    default void execute(REQUEST requestParam) {

    }

    /**
     * 执行策略，带返回值
     *
     * @param requestParam 执行策略入参
     * @return 执行策略后返回值
     */
    default RESPONSE executeResp(REQUEST requestParam) {
        return null;
    }
}
