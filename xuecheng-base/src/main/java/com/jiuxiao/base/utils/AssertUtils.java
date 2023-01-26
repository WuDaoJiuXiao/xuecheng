package com.jiuxiao.base.utils;

import com.jiuxiao.base.exception.XueChengException;

/**
 * 断言工具类
 * @Author: 悟道九霄
 * @Date: 2023年01月26日 11:11
 * @Version: 1.0.0
 */
public class AssertUtils {

    /**
     * @param expresion
     * @param message
     * @return: void
     * @decription 断言表达式为真，否则抛出异常
     * @date 2023/1/26 11:14
     */
    public static void isTrue(boolean expresion, String message) {
        if (!expresion) {
            throw new XueChengException(message);
        }
    }
}