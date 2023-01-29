package com.jiuxiao.content.controller;

import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.content.module.dto.AddCourseDto;
import com.jiuxiao.content.module.dto.CourseBaseInfoDto;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
import com.jiuxiao.content.module.dto.UpdateCourseDto;
import com.jiuxiao.content.module.po.CourseBase;
import com.jiuxiao.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("新增课程信息")
    @PostMapping("/course")
    public CourseBaseInfoDto addCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        //使用注解 @Validated 进行校验的对象，不合法时会抛出一个 MethodArgumentNotValidException，需要我们去捕获
        Long companyId = 1L; // 培训机构的id由授权认证模块来获得
        return courseBaseService.addCourseBase(companyId, addCourseDto);
    }

    @ApiOperation("根据ID获取课程基本、营销信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseInfoById(@PathVariable Long courseId){
        return courseBaseService.getCourseInfoById(courseId);
    }

    @ApiOperation("根据课程ID修改课程基本信息、营销信息")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseInfo(@RequestBody UpdateCourseDto updateCourseDto){
        Long companyId = 1L;
        return courseBaseService.updateCourse(companyId, updateCourseDto);
    }
}