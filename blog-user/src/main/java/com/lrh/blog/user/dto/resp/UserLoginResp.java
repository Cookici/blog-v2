package com.lrh.blog.user.dto.resp;

import com.lrh.blog.user.dao.UserModel;
import com.lrh.common.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.resp
 * @ClassName: UserLoginResp
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  UserLoginResp {

    private String userId;

    private String userName;

    private String userPhone;

    private Integer userLevel;

    private String userSex;

    private LocalDateTime userBirthday;

    private String userIp;

    private String userEmail;

    private String token;

    public UserLoginResp convertedUserModelToUserLoginResp(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        this.userId = userModel.getUserId();
        this.userName = userModel.getUserName();
        this.userPhone = userModel.getUserPhone();
        this.userLevel = userModel.getUserLevel();
        this.userSex = userModel.getUserSex();
        this.userBirthday = userModel.getUserBirthday();
        this.userIp = userModel.getUserIp();
        this.userEmail = userModel.getUserEmail();
        this.token = getToken(this.userId, this.userName, userModel.getRoleName());
        return this;
    }


    private String getToken(String userId, String userName, String roleName) {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        payload.put("roleName", roleName);
        return JwtUtil.getToken(payload);
    }

}
