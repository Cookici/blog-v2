package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.constant.BusinessConstant;
import com.lrh.blog.user.dto.UserValid;
import com.lrh.blog.user.dto.req.UserUpdateReq;
import com.lrh.blog.user.exception.ValidException;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserUpdateCmd
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午4:31
 */
@Getter
public class UserUpdateCmd {

    private final String userId;

    private final String userName;

    private final String userPassword;

    private final String userPhone;

    private final Integer userLevel;

    private final String userSex;

    private final LocalDateTime userBirthday;

    private final String userIp;

    private final String userEmail;

    private final String roleName;

    public UserUpdateCmd(UserUpdateReq req) throws ValidException {
        if (UserValid.validUserName(req.getUserName())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户名"));
        }
        if (UserValid.validUserPassword(req.getUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (UserValid.validUserPhone(req.getUserPhone())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "电话"));
        }
        if (UserValid.validUserLevel(req.getUserLevel())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户等级"));
        }
        if (UserValid.validUserSex(req.getUserSex())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户性别"));
        }
        if (UserValid.validUserBirthday(req.getUserBirthday())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户生日"));
        }
        if (UserValid.validUserIp(req.getUserIp())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "业务错误"));
        }
        if (UserValid.validUserEmail(req.getUserEmail())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "邮箱"));
        }
        if (UserValid.validUserRole(req.getRoleName())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "业务错误"));
        }
        this.userId = req.getUserId();
        this.userName = req.getUserName();
        this.userPassword = req.getUserPassword();
        this.userPhone = req.getUserPhone();
        this.userLevel = req.getUserLevel();
        this.userSex = req.getUserSex();
        this.userBirthday = req.getUserBirthday();
        this.userEmail = req.getUserEmail();
        this.userIp = req.getUserIp();
        this.roleName = req.getRoleName();
    }
}
