package com.jiuxiao.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
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
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.content.module.po.CourseBase>
     * @decription 查询课程基本信息列表
     * @date 2023/1/20 11:55
     */
    PageResult<CourseBase> queryCourseBaseInfo(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
}
