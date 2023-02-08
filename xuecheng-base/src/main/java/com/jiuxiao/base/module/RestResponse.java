package com.jiuxiao.base.module;

import lombok.Data;
import lombok.ToString;

/**
 * 上传视频信息实体类
 * @Author: 悟道九霄
 * @Date: 2023年02月08日 10:17
 * @Version: 1.0.0
 */
@Data
@ToString
public class RestResponse<T> {

    //响应编码：0为正常，-1为错误
    private int code;

    //响应信息
    private String msg;

    //响应结果
    private T result;

    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> RestResponse<T> validFail(String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> validFail(T result, String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setCode(-1);
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<T>();
        response.setResult(result);
        return response;
    }

    public static <T> RestResponse<T> success(T result, String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> success() {
        return new RestResponse<T>();
    }

    public Boolean isSuccessful() {
        return this.code == 0;
    }
}