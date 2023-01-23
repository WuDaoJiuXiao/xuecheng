package com.jiuxiao.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 系统管理模块主启动类
 * @Author: 悟道九霄
 * @Date: 2023年01月23日 12:01
 * @Version: 1.0.0
 */
@EnableScheduling
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}