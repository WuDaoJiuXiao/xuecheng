package com.jiuxiao.base.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间格式化配置类
 * @Author: 悟道九霄
 * @Date: 2023年01月20日 10:46
 * @Version: 1.0.0
 */
@Configuration
public class LocalDateTimeConfig {

    /**
     * @return: com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
     * @decription 序列化时间格式，服务端返回给客户端内容
     * @date 2023/1/20 10:48
     */
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @return: com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
     * @decription 时间反序列化，客户端传入服务端内容
     * @date 2023/1/20 10:51
     */
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @return: org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
     * @decription 配置序列化
     * @date 2023/1/20 10:52
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
        };
    }

}