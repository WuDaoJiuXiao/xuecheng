package com.jiuxiao.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuxiao.content.module.dto.TeachPlanDto;
import com.jiuxiao.content.module.po.TeachPlan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
public interface TeachPlanMapper extends BaseMapper<TeachPlan> {

    /**
     * @param courseId
     * @return: java.util.List<com.jiuxiao.content.module.dto.TeachPlanDto>
     * @decription 根据课程ID查询课程计划（树形结构）
     * @date 2023/1/29 10:41
     */
    List<TeachPlanDto> selectTreeNodes(Long courseId);
}
