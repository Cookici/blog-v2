package com.lrh.blog.user.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.req
 * @ClassName: UserRegisterReq
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午3:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterReq {

    private String userName;

    private String userPassword;

    private String userPasswordAgain;

    private String userPhone;

    private Integer userLevel;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userBirthday;

    private String userSex;

    private String userEmail;
}
