package com.jiuxiao.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus配置类
 * @Author: 悟道九霄
 * @Date: 2023年01月20日 11:21
 * @Version: 1.0.0
 */
@Configuration
@MapperScan("com.jiuxiao.content.mapper")
public class MyBatisPlusConfig {

    /**
     * @return: com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
     * @decription MyBatisPlus分页拦截器
     * @date 2023/1/20 11:23
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}