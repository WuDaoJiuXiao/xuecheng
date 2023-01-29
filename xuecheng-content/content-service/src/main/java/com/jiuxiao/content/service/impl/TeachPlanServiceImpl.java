package com.jiuxiao.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiuxiao.content.mapper.TeachPlanMapper;
import com.jiuxiao.content.module.dto.SaveTeachPlanDto;
import com.jiuxiao.content.module.dto.TeachPlanDto;
import com.jiuxiao.content.module.po.TeachPlan;
import com.jiuxiao.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachPlanMapper, TeachPlan> implements TeachPlanService {

    @Resource
    private TeachPlanMapper teachPlanMapper;

    /**
     * @param courseId 课程ID
     * @return: java.util.List<com.jiuxiao.content.module.dto.TeachPlanDto>
     * @decription 根据课程ID查询课程计划（树形结构）
     * @date 2023/1/29 14:31
     */
    @Override
    public List<TeachPlanDto> selectTreeNodes(Long courseId) {
        return teachPlanMapper.selectTreeNodes(courseId);
    }

    /**
     * @param saveTeachPlanDto 保存教学计划DTO
     * @return: void
     * @decription 存储、修改课程计划
     * @date 2023/1/29 14:55
     */
    @Override
    @Transactional
    public void saveOrUpdateTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        Long id = saveTeachPlanDto.getId();

        if (null == id) { //新增课程计划
            TeachPlan teachPlan = new TeachPlan();
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            Integer count = getOrderByNum(teachPlan.getCourseId(), teachPlan.getParentid());
            teachPlan.setOrderby(count + 1); //教学计划新章节的 orderBy 加一
            teachPlanMapper.insert(teachPlan);
        } else { //修改课程计划
            TeachPlan teachPlan = teachPlanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            teachPlanMapper.updateById(teachPlan);
        }
    }

    /**
     * @param courseId 课程ID
     * @param parentId 父节点ID
     * @return: int
     * @decription 计算新添加的课程计划的 OrderBy 数值：同级课程的计划数量 + 1
     * @date 2023/1/29 15:03
     */
    private Integer getOrderByNum(Long courseId, Long parentId) {
        LambdaQueryWrapper<TeachPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachPlan::getCourseId, courseId);
        queryWrapper.eq(TeachPlan::getParentid, parentId);
        return teachPlanMapper.selectCount(queryWrapper);
    }
}
