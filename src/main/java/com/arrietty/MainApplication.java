package com.arrietty;

import com.arrietty.service.FileStorageService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:13
 */
@MapperScan(basePackages = "com.arrietty.mapper")
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }

}
