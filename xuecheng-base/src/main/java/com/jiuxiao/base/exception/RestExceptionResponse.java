package com.jiuxiao.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 错误响应参数包装类
 * @Author: 悟道九霄
 * @Date: 2023年01月26日 10:48
 * @Version: 1.0.0
 */
@Data
@AllArgsConstructor
public class RestExceptionResponse {

    private String errMessage;
}