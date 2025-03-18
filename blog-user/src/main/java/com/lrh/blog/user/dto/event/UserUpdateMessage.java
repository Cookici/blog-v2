package com.lrh.blog.user.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String userName;
}