package com.jiuxiao.content.controller;


import com.jiuxiao.content.module.dto.SaveTeachPlanDto;
import com.jiuxiao.content.module.dto.TeachPlanDto;
import com.jiuxiao.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程计划 前端控制器
 * </p>
 *
 * @author 悟道九霄
 * @since 2023-01-23
 */
@Slf4j
@RestController
@Api(value = "课程计划管理", tags = "课程计划管理")
public class TeachPlanController {

    @Resource
    private TeachPlanService teachPlanService;

    @ApiOperation("根据课程ID查询课程计划")
    @GetMapping("/teach-plan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTeachPlan(@PathVariable Long courseId){
        return teachPlanService.selectTreeNodes(courseId);
    }

    @ApiOperation("存储、修改课程计划")
    @PostMapping("/teach-plan")
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto saveTeachPlanDto){
        teachPlanService.saveOrUpdateTeachPlan(saveTeachPlanDto);
    }
}
