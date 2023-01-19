package com.jiuxiao.base.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果类
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 12:57
 * @Version: 1.0.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    //数据列表
    private List<T> items;

    //总记录数
    private long counts;

    //当前页码
    private long currentPage;

    //每页记录数
    private long pageSize;
}