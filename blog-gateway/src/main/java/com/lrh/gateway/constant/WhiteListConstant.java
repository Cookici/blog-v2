package com.lrh.gateway.constant;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.gateway.constant
 * @ClassName: WhiteListContant
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 15:20
 */

public class WhiteListConstant {
    public static final String[] WHITE_LIST = {
            "/api/user/login",
            "/api/user/register",
            "/api/role/all_kinds",
            "/api/oss/file/upload",
            "/api/article/page",
            "/api/article/get",
            "/api/label/kinds",
            "/api/user/logout"
    };
}
