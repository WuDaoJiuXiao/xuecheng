package com.jiuxiao.content.module.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程计划
 * </p>
 *
 * @author 悟道九霄
 * @since 2023-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeachPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程计划名称
     */
    private String pName;

    /**
     * 课程计划父级Id
     */
    private Long parentId;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;

    /**
     * 开始直播时间
     */
    private LocalDateTime startTime;

    /**
     * 直播结束时间
     */
    private LocalDateTime endTime;

    /**
     * 章节及课程时介绍
     */
    private String description;

    /**
     * 时长，单位时:分:秒
     */
    private String timeLength;

    /**
     * 排序字段
     */
    private Integer orderBy;

    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;

    /**
     * 状态（1正常  0删除）
     */
    private Integer status;

    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;


}