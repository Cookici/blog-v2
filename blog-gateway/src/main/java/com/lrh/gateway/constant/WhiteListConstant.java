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
            "/api/comment/page_child",
            "/api/comment/page",
            "/api/label/kinds",
            "/api/article/view/no_login",
            "/api/article/like/no_login"
    };
}
