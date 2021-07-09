package com.arrietty.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.MessageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 17:51
 */

@Service
public class AvatarStorageService implements FileStorageService{

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg","jpeg","png");

    @Value("${file.avatar-image-path}")
    private String path;

    @Override
    public void init() {
        try {
            Files.createDirectory(Paths.get(path));
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot create folder for avatar images");
        }
    }

    @Override
    public String save(MultipartFile file){


    }

    private boolean isFileValid(MultipartFile file){
        if (file == null || file.getOriginalFilename() == null){
            throw new Logic
        }

    }
}
