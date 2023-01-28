package com.jiuxiao.base.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 分页查询参数类
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 12:43
 * @Version: 1.0.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {

    public static final long DEFAULT_PAGE_CURRENT = 1L;

    public static final long DEFAULT_PAGE_SIZE = 10L;

    //当前页数
    private Long pageNo = DEFAULT_PAGE_CURRENT;

    //每页记录数
    private Long pageSize = DEFAULT_PAGE_SIZE;
}