package com.lrh.blog.user.dto.cqe;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.blog.user.dto.valid.UserValid;
import com.lrh.blog.user.dto.req.UserUpdateReq;
import com.lrh.common.context.UserContext;
import com.lrh.common.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

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
        if(!Objects.equals(UserContext.getUserId(), req.getUserId())){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "非法操作"));
        }
        if(UserValid.validUserId(req.getUserId())){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验错误"));
        }
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
