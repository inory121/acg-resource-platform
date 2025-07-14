package com.acg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

/**
 * ACG资源管理平台主启动类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class
})
@MapperScan("com.acg.mapper")
public class AcgResourcePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcgResourcePlatformApplication.class, args);
    }
} 