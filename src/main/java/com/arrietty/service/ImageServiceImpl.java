package com.arrietty.service;

import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.ImageMapper;
import com.arrietty.entity.Image;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.SessionPO;
import com.arrietty.utils.session.SessionContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 17:51
 */

@Service
public class ImageServiceImpl {

    private static final String TEMP_FILE = "tmp";

    @Value("${file.base-image-directory}")
    private String BASE_PATH;

    @Value("${file.max-avatar-image-size}")
    private Integer MAX_AVATAR_SIZE;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ProfileServiceImpl profileService;

    private File tempFile;


    public void init() {
        Path avatarPath = Paths.get(BASE_PATH);
        if (!Files.exists(avatarPath)){
            try {
                Files.createDirectory(Paths.get(BASE_PATH));
            }
            catch (IOException e) {
                throw new RuntimeException("Cannot create folder for images.");
            }
        }
    }

    public void updateAvatar(MultipartFile file) throws LogicException{
        if(file==null || file.getSize()>MAX_AVATAR_SIZE){
            throw  new LogicException(ErrorCode.MAX_IMAGE_SIZE_EXCEEDED, "Image size exceeded.");
        }
        save(file);
    }

    public void getAvatar(HttpServletResponse response) throws LogicException {
        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        if(profilePO==null || profilePO.getAvatarImageId()==null){
            return;
        }

        try{
            final InputStream in = new FileInputStream(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+profilePO.getAvatarImageId().toString());
            //TODO: 图片格式的匹配问题, 找不到返回default 图片
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        }
        catch (FileNotFoundException e){
            throw new LogicException(ErrorCode.IMAGE_NOT_FOUND, "Image not found.");
        }
        catch (IOException e){
            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Image load failed.");
        }

    }


    private void save(MultipartFile file) throws LogicException{
        String fileFormat = null;
        try{
            tempFile = new File(BASE_PATH+"/"+TEMP_FILE);
            if(!tempFile.exists()){
                tempFile.createNewFile();
            }
        }
        catch (IOException e){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Open temp file failed");
        }

        try{
            fileFormat = file.getOriginalFilename().split("\\.")[1];
            file.transferTo(tempFile);
            if(ImageIO.read(tempFile)==null){
                throw new LogicException(ErrorCode.BAD_IMAGE_FORMAT, "Invalid image format.");
            }
        }
        catch (IOException e){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "File IO exception.");
        }

        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        if(profilePO==null){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Cannot find user profile.");
        };

        // update image table, update user profile if needed

        Image image = new Image();
        image.setImageType(fileFormat);
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());
        // new avatar
        if(profilePO.getAvatarImageId()==null){

            imageMapper.insertAndGetPrimaryKey(image);
            if(image.getId()==null){
                throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Insert to DB failed.");
            }
            profilePO.setAvatarImageId(image.getId());
            profileService.updateUserProfile(profilePO);

        }
        else{
            image.setId(profilePO.getAvatarImageId());
            imageMapper.updateByPrimaryKey(image);
        }

        // copy from tmp file to user folder
        FileChannel src = null;
        FileChannel dest = null;

        File fileFolder = new File(BASE_PATH+"/"+SessionContext.getUserNetId());
        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }

        File filePath = new File(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+image.getId().toString());
        if(filePath.exists()){
            filePath.delete();
        }
        try{
            filePath.createNewFile();
            src = new FileInputStream(tempFile).getChannel();
            dest = new FileOutputStream(filePath).getChannel();
            dest.transferFrom(src, 0, src.size());
            src.close();
            dest.close();

        }
        catch (IOException e){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "File system save failed");
        }
    }
//
//    @Override
//    public Resource load(String externalImageId) {
//        if (externalImageId == null || externalImageId.length()<32){
//            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Bad image id");
//        }
//
//        Image image = imageMapper.queryByExternalImageId(externalImageId);
//        if (image == null){
//            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Image not found");
//        }
//
//        Path imagePath = Paths.get(BASE_PATH + image.getExternalImageId() + "." + image.getImageFormat());
//        try {
//            Resource resource = new UrlResource(imagePath.toUri());
//            if (resource.exists() || resource.isReadable()){
//                return resource;
//            }
//        }
//        catch ( MalformedURLException e) {
//            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Bad directory");
//        }
//        return null;
//    }

}
