package com.lrh.blog.user.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.req
 * @ClassName: UserUpdateReq
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午4:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateReq {

    private String userId;

    private String userName;

    private String userSex;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userBirthday;
}
