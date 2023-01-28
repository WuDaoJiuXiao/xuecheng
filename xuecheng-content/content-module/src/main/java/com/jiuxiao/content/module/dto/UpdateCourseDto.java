package com.jiuxiao.content.module.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改课程信息DTO
 * @Author: 悟道九霄
 * @Date: 2023年01月28日 11:35
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateCourseDto extends AddCourseDto{

    @ApiModelProperty("课程ID")
    private Long id;
}