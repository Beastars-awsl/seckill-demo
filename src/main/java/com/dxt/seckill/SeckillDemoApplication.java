package com.dxt.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dxt.seckill.mapper")

public class SeckillDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillDemoApplication.class, args);
    }

}
