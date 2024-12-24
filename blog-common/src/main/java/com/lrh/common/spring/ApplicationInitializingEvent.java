package com.lrh.common.spring;

import org.springframework.context.ApplicationEvent;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.config.spring
 * @ClassName: ApplicationInitializingEvent
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 00:10
 */

public class ApplicationInitializingEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     * @param source source – the object on which the event initially occurred or with which the event is associated (never null)
     */
    public ApplicationInitializingEvent(Object source) {
        super(source);
    }
}