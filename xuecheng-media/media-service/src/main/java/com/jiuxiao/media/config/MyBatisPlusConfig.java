package com.jiuxiao.media.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus配置类
 * @Author: 悟道九霄
 * @Date: 2023年02月01日 16:18
 * @Version: 1.0.0
 */
@Configuration
@MapperScan("com.jiuxiao.media.mapper")
public class MyBatisPlusConfig {

    /**
     * @return: com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
     * @decription 配置分页插件
     * @date 2023/2/1 16:20
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}