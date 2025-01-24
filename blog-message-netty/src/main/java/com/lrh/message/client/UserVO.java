package com.lrh.message.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.client
 * @ClassName: UserVO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 19:25
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    private String userId;
    private String userName;
    private String userPhoto;
    private Integer userLevel;
}
