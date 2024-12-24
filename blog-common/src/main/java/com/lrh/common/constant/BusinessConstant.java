package com.lrh.common.constant;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.constant
 * @ClassName: BusinessConstant
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午3:26
 */

public class BusinessConstant {
    public final static String VALID_ERROR = "%s，不符合规范";
    public final static String NO_USER = "没有该用户或者账户密码错误";
    public final static String DUP_KEY = "数据已存在";
    public final static Integer IS_DELETED = 1;
    public final static Integer IS_NOT_DELETED = 0;
    public final static String LOGIN_FAIL_RECORD = "账号或密码错误";
    public final static Integer ID_MAX_LENGTH = 64;
}
