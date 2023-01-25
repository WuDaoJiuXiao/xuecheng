package com.jiuxiao.content.module.dto;

import com.jiuxiao.content.module.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 课程分类查询类
 * @Author: 悟道九霄
 * @Date: 2023年01月23日 16:27
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseCategoryTreeDto extends CourseCategory {

    //孩子节点
    private List<CourseCategoryTreeDto> childrenTreeNodes;
}