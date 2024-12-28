package com.lrh.blog.user.exception;

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
        return Result.fail().code(HttpStatus.UNAUTHORIZED.value()).message(BusinessConstant.NO_USER);
    }

    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Object> handleDuplicateKeyException() {
        return Result.fail().code(HttpStatus.BAD_REQUEST.value()).message(BusinessConstant.DUP_KEY);
    }

    @ResponseBody
    @ExceptionHandler(ValidException.class)
    public Result<Object> handleValidException(Exception e) {
        return Result.fail().code(HttpStatus.BAD_REQUEST.value()).message(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({AlgorithmMismatchException.class, SignatureVerificationException.class, TokenExpiredException.class})
    public Result<Object> handleJwtTokenException() {
        return Result.fail().code(HttpStatus.UNAUTHORIZED.value()).message(HttpStatus.UNAUTHORIZED.getReasonPhrase());
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
