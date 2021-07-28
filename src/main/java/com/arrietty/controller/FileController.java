package com.arrietty.controller;

import com.arrietty.consts.Api;
import com.arrietty.consts.ErrorCode;
import com.arrietty.entity.Profile;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ImageResponseType;
import com.arrietty.service.file.AvatarService;
import com.arrietty.service.file.FileStorageService;
import com.arrietty.utils.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/25 17:50
 */

@RestController
@RequestMapping(Api.SERVICE_VERSION)
public class FileController {

    private static final String AVATAR_TYPE = "avatar";

    @Autowired
    private AvatarService avatarService;

    @PostMapping(Api.IMAGE)
    @ResponseBody
    public Object postImage(@RequestParam("type") String type, @RequestParam("file") MultipartFile uploadedFile){

        if (type == null){
            throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "invalid type");
        }

        if (AVATAR_TYPE.equals(type)){
            return avatarService.saveAvatar(uploadedFile);
        }

        return null;
    }

    @GetMapping(Api.IMAGE)
    public ResponseEntity<Resource> getImage(@RequestParam("type") String type, @RequestParam(required = false, value = "imageId") String imageId){
        if (type == null){
            throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "invalid type");
        }

        if (AVATAR_TYPE.equals(type)){
            return avatarService.loadAvatar();
        }
        return null;
    }





}
