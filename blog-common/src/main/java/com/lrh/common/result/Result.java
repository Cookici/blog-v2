package com.lrh.common.result;

import lombok.Data;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.results
 * @ClassName: Result
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午1:46
 */

@Data
public class Result<T> {


    private Integer code;
    private String message;
    private T data;



    private Result(){}

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        if(body != null) {
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 成功
     */
    public static<T> Result<T> success() {
        return build(null,ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> success(T data) {
        return build(data,ResultCodeEnum.SUCCESS);
    }

    /**
     * 失败
     */
    public static<T> Result<T> fail() {
        return build(null,ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> fail(T data) {
        return build(data,ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

}