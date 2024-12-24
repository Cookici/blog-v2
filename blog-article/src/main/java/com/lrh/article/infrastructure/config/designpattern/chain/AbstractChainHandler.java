package com.lrh.article.infrastructure.config.designpattern.chain;

import org.springframework.core.Ordered;

/**
 * @ProjectName: project-12306
 * @Package: com.lrh.project12306.framework.starter.designpattern.chain
 * @ClassName: AbstractChainHandler
 * @Author: 63283
 * @Description: 抽象业务责任链组件
 * @Date: 2024/3/6 17:57
 */

public interface AbstractChainHandler<T> extends Ordered {
    /**
     * 执行责任链逻辑
     *
     * @param requestParam 责任链执行入参
     */
    void handler(T requestParam);

    /**
     * @return 责任链组件标识
     */
    String mark();
}
