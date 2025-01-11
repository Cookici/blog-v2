package com.lrh.blog.user.dto.valid;

import com.lrh.blog.user.constant.UserConstant;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto
 * @ClassName: valid
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午4:10
 */

public class UserValid {

    public static boolean validUserId(String userId){
        return userId == null;
    }

    public static boolean validUserName(String userName){
        return userName == null || userName.length() >= 64 || userName.length() <= 8;
    }

    public static boolean validUserPassword(String userPassword){
        return userPassword == null || userPassword.length() >= 64 || userPassword.length() <= 8;
    }

    public static boolean validUserPhone(String userPhone){
        return userPhone == null || !userPhone.matches("^\\d{11}$");
    }

    public static boolean validUserLevel(Integer userLevel){
        return userLevel == null || userLevel < 0;
    }

    public static boolean validUserSex(String userSex){
        return userSex == null || (!Objects.equals(userSex, UserConstant.SEX_MAN) && !Objects.equals(userSex, UserConstant.SEX_WOMAN));
    }

    public static boolean validUserBirthday(LocalDateTime userBirthday){
        return userBirthday == null;
    }

    public static boolean validUserRole(String roleName){
        return roleName == null || roleName.isEmpty() || roleName.length() > 20;
    }

    public static boolean validUserEmail(String userEmail){
        return userEmail == null || !userEmail.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    public static boolean validUserIp(String userIp){
        return userIp == null || !userIp.matches("(^((2[0-4]\\d.)|(25[0-5].)|(1\\d{2}.)|(\\d{1,2}.))((2[0-5]{2}.)|(1\\d{2}.)|(\\d{1,2}.){2})((1\\d{2})|(2[0-5]{2})|(\\d{1,2})))");
    }

}
