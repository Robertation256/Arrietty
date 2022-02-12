//package com.arrietty.controller;
//
//import com.arrietty.service.FileStorageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Map;
//
///**
// * @Author: Yuechuan Zhang
// * @Date: 2021/6/25 17:50
// */
//
//@RestController
//public class FileController {
//
//    @Autowired
//    private Map<String, FileStorageService> fileStorageServiceMap;
//
//    @PostMapping("/avatar")
//    @ResponseBody
//    public String upload(@RequestParam("file") MultipartFile uploadedFile){
//
//        FileStorageService avatarStorageService = fileStorageServiceMap.get("avatarStorageService");
//        return avatarStorageService.save(uploadedFile);
//
//    }
//
//
//
//
//}
