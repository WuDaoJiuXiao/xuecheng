package com.jiuxiao.system.controller;

import com.jiuxiao.system.module.po.Dictionary;
import com.jiuxiao.system.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据字典控制器
 * @Author: 悟道九霄
 * @Date: 2023年01月23日 11:58
 * @Version: 1.0.0
 */
@Slf4j
@RestController
@Api(value = "系统管理接口", tags = "系统管理接口")
public class DictionaryController {

    @Resource
    private DictionaryService dictionaryService;

    @GetMapping("/dictionary/all")
    @ApiOperation(value = "查询数据字典所有信息")
    public List<Dictionary> queryAll() {
        return dictionaryService.queryAll();
    }

    @ApiOperation(value = "通过代号获取对应的信息")
    @GetMapping("/dictionary/code/{code}")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }
}
