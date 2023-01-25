package com.jiuxiao.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.content.module.dto.CourseCategoryTreeDto;
import com.jiuxiao.content.module.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * @param id
     * @return: java.util.List<com.jiuxiao.content.module.dto.CourseCategoryTreeDto>
     * @decription 查询所有课程分类
     * @date 2023/1/23 16:55
     */
    List<CourseCategoryTreeDto> queryCourseCategoryList(String id);
}
