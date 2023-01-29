package com.jiuxiao.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.content.module.dto.SaveTeachPlanDto;
import com.jiuxiao.content.module.dto.TeachPlanDto;
import com.jiuxiao.content.module.po.TeachPlan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
public interface TeachPlanService extends IService<TeachPlan> {

    /**
     * @param courseId
     * @return: java.util.List<com.jiuxiao.content.module.dto.TeachPlanDto>
     * @decription 根据课程ID查询课程计划（树形结构）
     * @date 2023/1/29 14:31
     */
    List<TeachPlanDto> selectTreeNodes(Long courseId);

    /**
     * @param saveTeachPlanDto
     * @return: void
     * @decription 存储、修改课程计划
     * @date 2023/1/29 14:55
     */
    void saveOrUpdateTeachPlan(SaveTeachPlanDto saveTeachPlanDto);
}
