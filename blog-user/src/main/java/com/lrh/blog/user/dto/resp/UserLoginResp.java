package com.lrh.blog.user.dto.resp;

import com.lrh.blog.user.dao.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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

    private LocalDateTime creatTime;

    private String userIp;

    private String userEmail;

    private String userPhoto;

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
        this.userPhoto = userModel.getUserPhoto();
        this.userEmail = userModel.getUserEmail();
        this.creatTime = userModel.getCreateTime();
        return this;
    }




}
