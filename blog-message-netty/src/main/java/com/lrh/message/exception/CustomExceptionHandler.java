package com.lrh.message.exception;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.NoUserException;
import com.lrh.common.exception.ValidException;
import com.lrh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.exception
 * @ClassName: ExceptionHandler
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:39
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ResponseBody
    @ExceptionHandler(NoUserException.class)
    public Result<Object> handleNoUserException() {
        log.error("[CustomExceptionHandler] handleNoUserException error");
        return Result.fail().code(HttpStatus.UNAUTHORIZED.value()).message(BusinessConstant.NO_USER);
    }

    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Object> handleDuplicateKeyException() {
        log.error("[CustomExceptionHandler] handleDuplicateKeyException error");
        return Result.fail().code(HttpStatus.BAD_REQUEST.value()).message(BusinessConstant.DUP_KEY);
    }

    @ResponseBody
    @ExceptionHandler(ValidException.class)
    public Result<Object> handleValidException(Exception e) {
        log.error("[CustomExceptionHandler] handleDuplicateKeyException error: {}",e.getMessage());
        return Result.fail().code(HttpStatus.BAD_REQUEST.value()).message(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({AlgorithmMismatchException.class, SignatureVerificationException.class, TokenExpiredException.class})
    public Result<Object> handleJwtTokenException() {
        log.error("[CustomExceptionHandler] handleJwtTokenException error");
        return Result.fail().code(HttpStatus.UNAUTHORIZED.value()).message(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    /**
     * 处理请求参数格式错误 @RequestBody 上使用 @NotNull 等，验证失败后抛出的异常是 MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[CustomExceptionHandler] handleMethodArgumentNotValidException error: {}",e.getMessage());
        return Result.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 处理 Get 请求中 验证路径中请求实体校验失败后抛出的异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handlerBindException(BindException e) {
        log.error("[CustomExceptionHandler] handlerBindException error: {}",e.getMessage());
        return Result.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 参数格式异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Object> handlerHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[CustomExceptionHandler] handlerHttpMessageNotReadableException error: {}",e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(Exception e) {
        log.error("[CustomExceptionHandler] handleRuntimeException error : {}", e.getMessage());
        return Result.fail(e.getMessage()).code(HttpStatus.BAD_REQUEST.value());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("[CustomExceptionHandler] handleException error : {}", e.getMessage());
        return Result.fail().code(HttpStatus.BAD_REQUEST.value());
    }

}
