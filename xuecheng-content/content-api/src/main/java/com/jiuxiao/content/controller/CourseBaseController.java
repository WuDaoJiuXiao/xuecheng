package com.jiuxiao.content.controller;

import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
import com.jiuxiao.content.module.po.CourseBase;
import com.jiuxiao.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 课程基本信息控制器
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 13:21
 * @Version: 1.0.0
 */
@RestController
@Api(value = "课程信息接口", tags = "课程信息接口")
public class CourseBaseController {

    @Resource
    private CourseBaseService courseBaseService;

    @ApiOperation("课程列表查询")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseService.queryCourseBaseInfo(pageParams, queryCourseParamsDto);
    }
}