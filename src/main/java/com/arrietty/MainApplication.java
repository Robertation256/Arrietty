package com.arrietty;

import com.arrietty.service.file.FileStorageService;
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
public class MainApplication implements CommandLineRunner {

    @Autowired
    private Map<String, FileStorageService> fileStorageServiceMap;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }

    @Override
    public void run(String ... arg) throws Exception {
        FileStorageService avatarStorageService = fileStorageServiceMap.get("avatarStorageService");
        avatarStorageService.init();
    }
}
