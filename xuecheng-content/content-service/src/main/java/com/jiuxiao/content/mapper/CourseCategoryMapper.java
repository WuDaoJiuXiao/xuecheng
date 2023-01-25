package com.jiuxiao.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuxiao.content.module.dto.CourseCategoryTreeDto;
import com.jiuxiao.content.module.po.CourseCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * @param id
     * @return: java.util.List<com.jiuxiao.content.module.dto.CourseCategoryTreeDto>
     * @decription 查询所有分类列表
     * @date 2023/1/23 17:36
     */
    List<CourseCategoryTreeDto> queryCourseCategoryList(@Param("id") String id);
}
