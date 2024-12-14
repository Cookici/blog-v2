package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.constant.BusinessConstant;
import com.lrh.blog.user.dto.UserValid;
import com.lrh.blog.user.dto.req.UserUpdateReq;
import com.lrh.blog.user.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;
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

    private final String userSex;

    private final LocalDateTime userBirthday;

    public UserUpdateCmd(@NotNull UserUpdateReq req) throws ValidException {
        if (UserValid.validUserName(req.getUserName())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户名"));
        }
        if (UserValid.validUserSex(req.getUserSex())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户性别"));
        }
        if (UserValid.validUserBirthday(req.getUserBirthday())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户生日"));
        }
        this.userId = req.getUserId();
        this.userName = req.getUserName();
        this.userSex = req.getUserSex();
        this.userBirthday = req.getUserBirthday();
    }
}
