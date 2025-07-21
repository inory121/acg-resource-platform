package com.acg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序主入口
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class
})
@MapperScan("com.acg.mapper")
@EnableScheduling
public class AcgResourcePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcgResourcePlatformApplication.class, args);
    }
} 