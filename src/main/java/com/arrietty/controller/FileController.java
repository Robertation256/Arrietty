package com.arrietty.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/25 17:50
 */

@RestController
@RequestMapping("/serviceV0")
public class FileController {

    @Value("${file.image-path}")
    private String imageStoragePath;

    @PostMapping("/avatar")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile uploadedFile){
        String fileName = uploadedFile.getOriginalFilename();
        try {
            uploadedFile.transferTo(new File(imageStoragePath+fileName));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return "Success";

    }




}
