package com.jiuxiao.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.system.module.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * @return: java.util.List<com.jiuxiao.system.module.po.Dictionary>
     * @decription 查询所有数据字典内容
     * @date 2023/1/23 11:52
     */
    List<Dictionary> queryAll();

    /**
     * @param code
     * @return: com.jiuxiao.system.module.po.Dictionary
     * @decription 根据code查询数据字典
     * @date 2023/1/23 11:52
     */
    Dictionary getByCode(String code);
}
