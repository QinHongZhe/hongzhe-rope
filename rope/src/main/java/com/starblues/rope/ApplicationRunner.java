package com.starblues.rope;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.starblues.rope.repository.mapper")
public class ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

}
