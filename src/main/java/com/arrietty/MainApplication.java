package com.arrietty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:13
 */

@SpringBootApplication
@MapperScan(basePackages = "com.arrietty.mapper")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}
