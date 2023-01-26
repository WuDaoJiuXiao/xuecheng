package com.jiuxiao.base.exception;

/**
 * 通用异常枚举类
 * @Author: 悟道九霄
 * @Date: 2023年01月26日 10:31
 * @Version: 1.0.0
 */
public enum CommonException {

    UN_KNOW_ERROR("未知异常，请联系管理员"),
    PARAMS_ERROR("非法参数"),
    OBJECT_EMPTY_ERROR("对象为空"),
    QUERY_EMPTY_ERROR("查询结果为空"),
    REQUEST_PARAMS_EMPTY_ERROR("请求参数为空");

    private String errMessage;

    CommonException() {
    }

    CommonException(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return this.errMessage;
    }
}
