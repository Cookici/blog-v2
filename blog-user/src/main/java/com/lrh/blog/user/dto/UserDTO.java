package com.lrh.blog.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String userId;

    private String userName;

    private String userPhone;

    private Integer userLevel;

    private String userSex;

    private LocalDateTime userBirthday;

    private String userIp;

    private String userPhoto;

    private String userEmail;

    private LocalDateTime creatTime;

}
