package com.arrietty;


import com.arrietty.service.ImageServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:13
 */

@MapperScan(basePackages = "com.arrietty.mapper")
@EnableTransactionManagement
@SpringBootApplication
public class MainApplication implements CommandLineRunner{

    @Autowired
    private ImageServiceImpl imageService;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        imageService.init();
    }
}
