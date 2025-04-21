package com.lrh.blog.user.dto.resp;

import com.lrh.blog.user.dto.UserDTO;
import com.lrh.blog.user.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    private UserDTO userInfo;

    private String token;

    public UserLoginResp convertedUserModelToUserLoginResp(UserModel userModel,String token) {
        if (userModel == null) {
            return null;
        }
        this.userInfo = new UserDTO(
                userModel.getUserId(),
                userModel.getUserName(),
                userModel.getUserPhone(),
                userModel.getUserLevel(),
                userModel.getUserSex(),
                userModel.getUserBirthday(),
                userModel.getUserIp(),
                userModel.getUserPhoto(),
                userModel.getUserEmail(),
                userModel.getCreateTime()
        );
        this.token = token;
        return this;
    }
}
