package com.lrh.gateway.result;

import lombok.Getter;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.result
 * @ClassName: ResultCodeEnum
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午1:47
 */

@Getter
public enum ResultCodeEnum {

    /**
     * SUCCESS 请求成功
     * FAIL 请求失败
     */
    SUCCESS(200,"请求成功"),
    FAIL(400, "请求失败");


    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
