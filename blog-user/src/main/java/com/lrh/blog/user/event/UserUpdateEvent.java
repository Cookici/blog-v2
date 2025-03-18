package com.lrh.blog.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserUpdateEvent extends ApplicationEvent {
    private final String userId;
    private final String userName;

    public UserUpdateEvent(Object source, String userId, String userName) {
        super(source);
        this.userId = userId;
        this.userName = userName;
    }
}