package com.lrh.blog.user.dto.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lrh.blog.user.serializer.PhoneSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStausVO {
    private String userId;
    private String userName;
    @JsonSerialize(using = PhoneSerializer.class)
    private String userPhone;
    private String userEmail;
    private LocalDateTime createTime;
    private Integer status;
}

