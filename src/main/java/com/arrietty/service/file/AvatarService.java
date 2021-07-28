package com.arrietty.service.file;

import com.arrietty.pojo.ImageResponseType;
import com.arrietty.utils.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/28 14:12
 */

@Service
public class AvatarService {
    @Autowired
    private AvatarStorageService avatarStorageService;

    public Response<ImageResponseType> saveAvatar(MultipartFile uploadedFile){
        String imageId =  avatarStorageService.save(uploadedFile);
        imageId = imageId==null?"":imageId;
        ImageResponseType imageResponseType = new ImageResponseType();
        imageResponseType.setImageId(imageId);
        return Response.buildSuccessResponse(ImageResponseType.class, imageResponseType);
    }

    public ResponseEntity<Resource> loadAvatar(){
        return avatarStorageService.load();
    }

}
