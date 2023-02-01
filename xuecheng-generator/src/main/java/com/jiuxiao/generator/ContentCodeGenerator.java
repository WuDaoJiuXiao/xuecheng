package com.jiuxiao.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Arrays;

/**
 * 内容管理模块代码生成器
 * @Author: 悟道九霄
 * @Date: 2023年01月18日 11:29
 * @Version: 1.0.0
 */
public class ContentCodeGenerator {

    private static final String SERVICE_NAME = "media";

    private static final Boolean IS_DTO = false;

    private static final String[] TABLE_NAME_LIST = new String[]{
            "media_files",
            "media_process",
            "media_process_history",
            "mq_message",
            "mq_message_history",
    };

    private static final String baseDir = System.getProperty("user.dir");

    private static final String DATA_SOURCES_USERNAME = "root";

    private static final String DATA_SOURCE_PASSWORD = "0531";

    private static final String DATA_SOURCE_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";

    private static final String DATA_SOURCE_URL = "jdbc:mysql://127.0.0.1:3306/xuecheng_" + SERVICE_NAME + "?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai";

    public static void main(String[] args) {

        AutoGenerator autoGenerator = new AutoGenerator();

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig()
                .setFileOverride(true)
                .setOutputDir(baseDir + "/xuecheng-generator/src/main/java")
                .setAuthor("悟道九霄")
                .setOpen(false)
                .setServiceName("%sService")
                .setSwagger2(false)
                .setBaseResultMap(true)
                .setBaseColumnList(true);
        if (IS_DTO) {
            globalConfig.setSwagger2(true);
            globalConfig.setEntityName("%sDTO");
        }

        //数据库配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setUsername(DATA_SOURCES_USERNAME)
                .setPassword(DATA_SOURCE_PASSWORD)
                .setDriverName(DATA_SOURCE_DRIVER_NAME)
                .setUrl(DATA_SOURCE_URL);

        //包配置
        PackageConfig packageConfig = new PackageConfig()
                .setModuleName(SERVICE_NAME)
                .setParent("com.jiuxiao")
                .setServiceImpl("service.impl")
                .setXml("mapper")
                .setEntity("po");

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setInclude(TABLE_NAME_LIST)
                .setControllerMappingHyphenStyle(true)
                .setTablePrefix(packageConfig.getModuleName() + "-")
                .setEntityBooleanColumnRemoveIsPrefix(true)
                .setTableFillList(Arrays.asList(
                        new TableFill("create_date", FieldFill.INSERT),
                        new TableFill("change_date", FieldFill.INSERT_UPDATE),
                        new TableFill("modify_date", FieldFill.UPDATE)
                ));

        //配置汇总
        autoGenerator.setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setPackageInfo(packageConfig)
                .setStrategy(strategyConfig)
                .setTemplate(new TemplateConfig())
                .setTemplateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}