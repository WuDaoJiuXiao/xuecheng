package com.jiuxiao.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.content.module.dto.AddCourseDto;
import com.jiuxiao.content.module.dto.CourseBaseInfoDto;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
import com.jiuxiao.content.module.dto.UpdateCourseDto;
import com.jiuxiao.content.module.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
public interface CourseBaseService extends IService<CourseBase> {

    /**
     * @param pageParams           分页参数
     * @param queryCourseParamsDto 查询条件
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.content.module.dto.CourseBaseInfoDto>
     * @decription 查询课程基本信息列表
     * @date 2023/1/20 11:55
     */
    PageResult<CourseBaseInfoDto> queryCourseBaseInfo(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @param companyId    培训机构的ID
     * @param addCourseDto 新增课程信息DTO
     * @return: com.jiuxiao.content.module.dto.CourseBaseInfoDto
     * @decription 新增课程基本信息、营销信息
     * @date 2023/1/25 12:40
     */
    CourseBaseInfoDto addCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * @param courseId 课程ID
     * @return: com.jiuxiao.content.module.dto.CourseBaseInfoDto
     * @decription 通过课程ID查询课程的基本信息、营销信息
     * @date 2023/1/28 11:44
     */
    CourseBaseInfoDto getCourseInfoById(Long courseId);

    /**
     * @param companyId       机构ID
     * @param updateCourseDto 更新课程信息DTO
     * @return: com.jiuxiao.content.module.dto.CourseBaseInfoDto
     * @decription 更新课程基本信息、营销信息
     * @date 2023/1/28 11:49
     */
    CourseBaseInfoDto updateCourse(Long companyId, UpdateCourseDto updateCourseDto);
}
