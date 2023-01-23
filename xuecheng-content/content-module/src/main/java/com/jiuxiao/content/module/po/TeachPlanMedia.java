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
 * 
 * </p>
 *
 * @author 悟道九霄
 * @since 2023-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeachPlanMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 媒资文件id
     */
    private String mediaId;

    /**
     * 课程计划标识
     */
    private Long teachPlanId;

    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 媒资文件原始名称
     */
    private String mediaFileName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 修改人
     */
    private String changePeople;


}
