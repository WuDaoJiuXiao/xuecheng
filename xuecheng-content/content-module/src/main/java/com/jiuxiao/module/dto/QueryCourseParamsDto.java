package com.jiuxiao.module.dto;

import lombok.Data;

/**
 * 查询课程参数类
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 12:48
 * @Version: 1.0.0
 */
@Data
public class QueryCourseParamsDto {

    private String auditStatus;

    private String courseName;

    private String publishStatus;
}