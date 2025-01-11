package com.lrh.blog.user.dto.resp;

import com.lrh.blog.user.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.resp
 * @ClassName: UserRegisterResp
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午3:02
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResp {
    private String userName;

    public UserRegisterResp convertedUserModelToUserLoginResp(UserModel userModel){
        if (userModel == null) {
            return null;
        }
        this.userName = userModel.getUserName();
        return this;
    }
}
