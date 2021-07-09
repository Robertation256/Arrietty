package com.arrietty.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 17:36
 */
public interface FileStorageService {

    void init();

    //return fileId
    String save(MultipartFile file);

    void load(String fileId);

    void delete(List<String> fileIds);
}
