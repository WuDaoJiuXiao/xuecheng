package com.jiuxiao.api;

import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.module.dto.QueryCourseParamsDto;
import com.jiuxiao.module.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 课程基本信息控制器
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 13:21
 * @Version: 1.0.0
 */
@Controller
@Api(value = "课程信息接口", tags = "课程信息接口")
public class CourseBaseController {

    @ApiOperation("课程列表查询")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        return null;
    }
}