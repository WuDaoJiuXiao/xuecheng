package com.jiuxiao.content.module.dto;

import com.jiuxiao.content.module.po.TeachPlan;
import com.jiuxiao.content.module.po.TeachPlanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 教学计划DTO
 * @Author: 悟道九霄
 * @Date: 2023年01月29日 10:24
 * @Version: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class TeachPlanDto extends TeachPlan {

    //课程关联的媒资信息
    private TeachPlanMedia teachPlanMedia;

    //课程计划的子节点
    private List<TeachPlanDto> teachPlanTreeNodes;
}