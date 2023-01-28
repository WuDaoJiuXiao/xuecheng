package com.jiuxiao.content.module.dto;

import com.jiuxiao.content.module.po.CourseBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程基本信息DTO
 * @Author: 悟道九霄
 * @Date: 2023年01月25日 12:28
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseBaseInfoDto extends CourseBase {

    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 价格
     */
    private Float price;


    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称
     */
    private String mt;

    /**
     * 小分类名称
     */
    private String st;
}