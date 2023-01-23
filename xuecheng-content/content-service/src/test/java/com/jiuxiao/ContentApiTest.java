package com.jiuxiao;

import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.content.mapper.CourseBaseMapper;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
import com.jiuxiao.content.module.po.CourseBase;
import com.jiuxiao.content.service.CourseBaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 内容管理测试
 * @Author: 悟道九霄
 * @Date: 2023年01月20日 11:29
 * @Version: 1.0.0
 */
@SpringBootTest
public class ContentApiTest {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseBaseService courseBaseService;

    @Test
    void testCourseBaseQuery(){
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);
    }

    @Test
    void testCourseBaseService(){
        PageResult<CourseBase> courseBasePageResult = courseBaseService.queryCourseBaseInfo(
                new PageParams(),
                new QueryCourseParamsDto()
        );
        System.out.println(courseBasePageResult);
    }
}