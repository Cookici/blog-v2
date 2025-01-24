package com.lrh.message.dto.resp;

import com.lrh.message.client.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.resp
 * @ClassName: FirendResp
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 19:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp {

    private UserVO userInfo;
    private String friendName;

}
