package com.jiuxiao.base.exception;

/**
 * 自定义异常类
 * @Author: 悟道九霄
 * @Date: 2023年01月26日 10:30
 * @Version: 1.0.0
 */
public class XueChengException extends RuntimeException {

    private String errMessage;

    public XueChengException() {
    }

    public XueChengException(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return this.errMessage;
    }

    public static void cast(String errMessage) {
        throw new XueChengException(errMessage);
    }

    public void cast(CommonException commonException) {
        throw new XueChengException(commonException.getErrMessage());
    }
}