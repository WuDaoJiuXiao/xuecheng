package com.jiuxiao.content.module.dto;

import lombok.Data;

/**
 * 查询课程参数类
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 12:48
 * @Version: 1.0.0
 */
@Data
public class QueryCourseParamsDto {

    //课程名
    private String courseName;

    //课程审核状态
    private String auditStatus;

    //课程类型
    private String courseType;

    //课程发布状态
    private String publishStatus;
}