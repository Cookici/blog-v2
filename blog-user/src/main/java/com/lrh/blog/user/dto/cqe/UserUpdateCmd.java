package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.dto.req.UserUpdateReq;
import com.lrh.blog.user.dto.valid.UserValid;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.exception.ValidException;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息更新命令
 */
@Data
public class UserUpdateCmd {

    private String userId;
    private String userName;
    private String userSex;
    private LocalDateTime userBirthday;

    private Map<String, Boolean> updateFields = new HashMap<>();

    public UserUpdateCmd(UserUpdateReq req) {
        this.userId = UserContext.getUserId();

        if (req.getUserName() != null) {
            if (UserValid.validUserName(req.getUserName())) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户名"));
            }
            this.userName = req.getUserName();
            updateFields.put("userName", true);
        }
        
        if (req.getUserSex() != null) {
            if (UserValid.validUserSex(req.getUserSex())) {
                throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户性别"));
            }
            this.userSex = req.getUserSex();
            updateFields.put("userSex", true);
        }
        
        if (req.getUserBirthday() != null) {
            this.userBirthday = req.getUserBirthday();
            updateFields.put("userBirthday", true);
        }
    }
    
    public boolean isFieldUpdated(String fieldName) {
        return updateFields.getOrDefault(fieldName, false);
    }
}
