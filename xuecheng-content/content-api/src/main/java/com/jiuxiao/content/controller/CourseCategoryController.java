package com.jiuxiao.content.controller;


import com.jiuxiao.content.module.dto.CourseCategoryTreeDto;
import com.jiuxiao.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程分类 前端控制器
 * </p>
 *
 * @author 悟道九霄
 * @since 2023-01-23
 */
@RestController
@RequestMapping("/course-category")
@Api(value = "课程分类接口",tags = "课程分类接口")
public class CourseCategoryController {

    @Resource
    private CourseCategoryService courseCategoryService;

    @GetMapping("/tree-nodes")
    @ApiOperation("查询所有课程分类")
    public List<CourseCategoryTreeDto> queryCourseCategory(){
        return courseCategoryService.queryCourseCategoryList("1");
    }
}
