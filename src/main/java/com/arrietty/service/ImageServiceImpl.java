package com.arrietty.service;

import com.arrietty.annotations.Log;
import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.ImageMapper;
import com.arrietty.entity.Image;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.utils.session.SessionContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 17:51
 */

@Service
public class ImageServiceImpl {

    @Value("${file.base-image-directory}")
    private String BASE_PATH;

    @Value("${file.max-avatar-image-size}")
    private Integer MAX_AVATAR_SIZE;

    @Value("${file.default-user-avatar-path}")
    private String DEFAULT_AVATAR_PATH;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ProfileServiceImpl profileService;



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


    public void getImage(Long id, HttpServletResponse response) throws LogicException {
        if (id==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid image id");
        }
        // image id 路径的缓存？？

        Image image = imageMapper.selectByPrimaryKey(id);
        if (image==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid image id");
        }

        String userNetId = profileService.getUserProfile(image.getUserId()).getNetId();

        // TODO: 图片获取接口复用
        try{
            final InputStream in = new FileInputStream(BASE_PATH+"/"+userNetId+"/"+id.toString());

            //TODO: 图片格式的匹配问题, 目前默认返回JPEG
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


    public void getAvatar(HttpServletResponse response) throws LogicException {
        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        boolean getDefaultAvatar = false;

        if(profilePO==null || profilePO.getAvatarImageId()==null){
            getDefaultAvatar=true;
        }

        try{
            final InputStream in;
            if(getDefaultAvatar){
                in = new FileInputStream(BASE_PATH+DEFAULT_AVATAR_PATH);
            }
            else{
                in = new FileInputStream(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+profilePO.getAvatarImageId().toString());
            }
            //TODO: 图片格式的匹配问题, 目前默认返回JPEG
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

    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(MultipartFile file) throws LogicException{

        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        if(profilePO==null){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Cannot find user profile.");
        };

        File tempFile = checkFileFormat(file);
        // update image table, update user profile if needed

        Image image = new Image();
        image.setImageType("avatar");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());
        // new avatar
        if(profilePO.getAvatarImageId()==null){

            imageMapper.insertAndGetPrimaryKey(image);
            if(image.getId()==null){
                tempFile.delete();
                throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Insert to DB failed.");
            }
            profilePO.setAvatarImageId(image.getId());
            profileService.updateUserProfile(profilePO);

        }
        else{
            image.setId(profilePO.getAvatarImageId());
            imageMapper.updateByPrimaryKey(image);
        }

        save(tempFile, image.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long insertAdvertisementImage(MultipartFile file) throws LogicException {
        File tempFile = checkFileFormat(file);

        Image image = new Image();
        image.setImageType("advertisement");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());

        imageMapper.insertAndGetPrimaryKey(image);
        if(image.getId()==null){
            tempFile.delete();
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Insert to DB failed.");
        }

        save(tempFile, image.getId());
        return image.getId();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Long updateAdvertisementImage(MultipartFile file, Long imageId) throws LogicException {
        File tempFile = checkFileFormat(file);
        // update image table, update user profile if needed

        Image image = new Image();
        image.setId(imageId);
        image.setImageType("advertisement");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());

        int count = imageMapper.updateByPrimaryKey(image);
        if(count==0){
            tempFile.delete();
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Update DB failed.");
        }

        save(tempFile, image.getId());
        return image.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long imageId) throws LogicException {

        int count = imageMapper.deleteByPrimaryKey(imageId);
        if(count==0){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Image does not exist");
        }

        File file = new File(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+imageId.toString());
        file.delete();
    }

    // copy from tmp file to user folder
    private void save(File file, Long imageId) throws LogicException{
        FileChannel src = null;
        FileChannel dest = null;

        File fileFolder = new File(BASE_PATH+"/"+SessionContext.getUserNetId());
        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }

        File filePath = new File(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+imageId.toString());
        //overwrite old file
        if(filePath.exists()){
            filePath.delete();
        }

        try{
            filePath.createNewFile();
            src = new FileInputStream(file).getChannel();
            dest = new FileOutputStream(filePath).getChannel();
            dest.transferFrom(src, 0, src.size());
            src.close();
            dest.close();

        }
        catch (IOException e){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "File system save failed");
        }
        finally {
            file.delete();
        }
    }



    public void deleteImageFiles(List<String> imageIds){
        for(String imageId: imageIds){
            try{
                File file = new File(BASE_PATH+"/"+SessionContext.getUserNetId()+"/"+imageId);
                file.delete();
            }
            catch (Exception e){
                // TODO: log4j 日志
                e.printStackTrace();
            }
        }
    }



    private File checkFileFormat(MultipartFile file) throws LogicException{
        if(file==null || file.getSize()>MAX_AVATAR_SIZE){
            throw  new LogicException(ErrorCode.MAX_IMAGE_SIZE_EXCEEDED, "Image size exceeded.");
        }
        // use thread name to create temp file, avoid multiple thread writing to the same file
        File tempFile = new File(BASE_PATH+"/"+Thread.currentThread().getName()+"-tmp");
        try{
            tempFile.createNewFile();
        }
        catch (IOException e){
            tempFile.delete();
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Open temp file failed");
        }

        try{
            file.transferTo(tempFile);
            if(ImageIO.read(tempFile)==null){
                throw new LogicException(ErrorCode.BAD_IMAGE_FORMAT, "Invalid image format.");
            }
        }
        catch (IOException e){
            tempFile.delete();
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "File IO exception.");
        }
        return tempFile;
    }

}
