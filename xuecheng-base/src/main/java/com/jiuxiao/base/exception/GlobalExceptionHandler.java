package com.jiuxiao.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 全局异常处理类
 * @Author: 悟道九霄
 * @Date: 2023年01月26日 10:42
 * @Version: 1.0.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param e
     * @return: com.jiuxiao.base.exception.RestExceptionResponse
     * @decription 可预知异常处理
     * @date 2023/1/26 10:55
     */
    @ResponseBody
    @ExceptionHandler(XueChengException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handlerXueChengException(XueChengException e) {
        String errMessage = e.getErrMessage();
        log.error("捕获异常信息 : " + errMessage);
        return new RestExceptionResponse(errMessage);
    }

    /**
     * @param e
     * @return: com.jiuxiao.base.exception.RestExceptionResponse
     * @decription 不可预知异常处理
     * @date 2023/1/26 10:58
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handlerException(Exception e) {
        String errMessage = e.getMessage();
        log.error("捕获异常信息 : " + errMessage);
        return new RestExceptionResponse(CommonException.UN_KNOW_ERROR.getErrMessage());
    }

    /**
     * @param e
     * @return: com.jiuxiao.base.exception.RestExceptionResponse
     * @decription JSR303校验异常
     * @date 2023/1/26 11:56
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        fieldErrors.forEach(error -> {
            sb.append(error.getDefaultMessage()).append(",");
        });

        return new RestExceptionResponse(sb.toString());
    }
}