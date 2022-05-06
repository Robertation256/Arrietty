package com.arrietty.service;

import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.ImageMapper;
import com.arrietty.dao.UserMapper;
import com.arrietty.entity.Image;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.utils.session.SessionContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 17:51
 */

@Service
public class ImageServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Value("${file.base-image-directory}")
    private String BASE_PATH;

    @Value("${file.max-avatar-image-size}")
    private Integer MAX_AVATAR_SIZE;

    @Value("${file.max-ad-image-size}")
    private Integer MAX_AD_IMAGE_SIZE;

    @Value("${file.default-user-avatar-path}")
    private String DEFAULT_AVATAR_PATH;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    DataSourceTransactionManager transactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;



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
        Long userId = getOwnerIdByImageId(id);
        String filePath = BASE_PATH+"/user#"+userId.toString()+"/"+id.toString();
        readFileIntoServletResponse(filePath, response);
    }

    public void getAvatar(HttpServletResponse response) throws LogicException {
        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        boolean getDefaultAvatar = profilePO==null || profilePO.getAvatarImageId()==null;
        String filePath;
        if(getDefaultAvatar){
            filePath = DEFAULT_AVATAR_PATH;
        }
        else {
            filePath = BASE_PATH+"/user#"+SessionContext.getUserId()+"/"+profilePO.getAvatarImageId().toString();
        }
        readFileIntoServletResponse(filePath, response);
    }

    private void readFileIntoServletResponse(String filePath, HttpServletResponse response) throws LogicException {
        try{
            final InputStream in = new FileInputStream(filePath);
            //return image as JPEG type
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        }
        catch (FileNotFoundException e){
            throw new LogicException(ErrorCode.IMAGE_NOT_FOUND, "Image not found.");
        }
        catch (IOException e){
            throw new LogicException(ErrorCode.INTERNAL_ERROR, "Image load failed.");
        }
    }


    public void updateAvatar(MultipartFile file) throws Exception{
        ProfilePO profilePO = profileService.getUserProfile(SessionContext.getUserId());
        if(profilePO==null){
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Cannot find user profile.");
        };

        File tempFile = checkFileFormat(file, "avatar");
        Image image = new Image();
        image.setImageType("avatar");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());
        image.setUploadTime(new Date());

        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        if(profilePO.getAvatarImageId()==null){
            imageMapper.insertAndGetPrimaryKey(image);
            if(image.getId()==null){
                tempFile.delete();
                logger.error("[avatar update failed] failed to insert to db");
                transactionManager.rollback(transactionStatus);
                throw new LogicException(ErrorCode.INTERNAL_ERROR, "Save image failed");
            }
            userMapper.updateUserAvatarImageId(SessionContext.getUserId(), image.getId());
        }
        else{
            image.setId(profilePO.getAvatarImageId());
            imageMapper.updateByPrimaryKey(image);
        }


        try{
            save(tempFile, image.getId());
        }
        catch (Exception e){
            logger.error("[avatar update failed]", e);
            transactionManager.rollback(transactionStatus);
            throw e;
        }

        transactionManager.commit(transactionStatus);


        //update redis
        synchronized (ProfileServiceImpl.PROFILE_WRITE_LOCK){
            profilePO = profileService.getUserProfile(SessionContext.getUserId());
            profilePO.setAvatarImageId(image.getId());
            redisService.setUserProfile(SessionContext.getUserId(), profilePO);
        }
    }




    @Transactional(rollbackFor = Exception.class)
    public Long insertAdvertisementImage(MultipartFile file) throws LogicException {
        File tempFile = checkFileFormat(file, "advertisement");

        Image image = new Image();
        image.setImageType("advertisement");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());
        image.setUploadTime(new Date());

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
        File tempFile = checkFileFormat(file, "advertisement");
        // update image table, update user profile if needed

        Image image = new Image();
        image.setId(imageId);
        image.setImageType("advertisement");
        image.setImageSize((int)(file.getSize()/1024));
        image.setUserId(SessionContext.getUserId());
        image.setUploadTime(new Date());

        int count = imageMapper.updateByPrimaryKey(image);
        if(count==0){
            tempFile.delete();
            throw new LogicException(ErrorCode.IMAGE_SAVE_ERROR, "Update DB failed.");
        }

        save(tempFile, image.getId());
        return image.getId();
    }


    public void deleteImage(Long imageId){
        imageMapper.deleteByPrimaryKey(imageId);
        redisService.removeImageIdToOwnerIdCacheByImageId(imageId);
        File file = new File(BASE_PATH+"/user#"+SessionContext.getUserId()+"/"+imageId.toString());
        file.delete();
    }

    // copy from tmp file to user folder
    private void save(File file, Long imageId) throws LogicException{
        FileChannel src = null;
        FileChannel dest = null;

        File fileFolder = new File(BASE_PATH+"/user#"+SessionContext.getUserId());
        if(!fileFolder.exists()){
            if(!fileFolder.mkdir()){
                logger.error("[image save failed] cannot create folder for user: "+SessionContext.getUserNetId());
                throw new LogicException(ErrorCode.INTERNAL_ERROR, "Save image failed");
            }
        }

        File filePath = new File(BASE_PATH+"/user#"+SessionContext.getUserId()+"/"+imageId.toString());
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
            logger.error("[image save failed]", e);
            throw new LogicException(ErrorCode.INTERNAL_ERROR, "temp file transfer failed");
        }
        finally {
            file.delete();
        }
    }



    public void deleteImageFiles(List<String> imageIds){
        for(String imageId: imageIds){
            try{
                File file = new File(BASE_PATH+"/user#"+SessionContext.getUserId()+"/"+imageId);
                file.delete();
            }
            catch (Exception e){
                logger.error("[remove image file failed] ", e);
            }
        }
    }



    private File checkFileFormat(MultipartFile file, String imageType) throws LogicException{
        if(file==null){
            throw  new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Image file is empty");
        }

        if(("avatar".equals(imageType) && file.getSize()>MAX_AVATAR_SIZE) ||
                ("advertisement".equals(imageType) && file.getSize()>MAX_AD_IMAGE_SIZE)
        ){
            throw  new LogicException(ErrorCode.MAX_IMAGE_SIZE_EXCEEDED, "Image size exceeded.");
        }

        // use thread name to create temp file, avoid multiple thread writing to the same file
        String tempFilePath = BASE_PATH+"/"+Thread.currentThread().getName()+"-tmp";
        File tempFile = new File(tempFilePath);
        try{
            tempFile.createNewFile();
        }
        catch (IOException e){
            tempFile.delete();
            logger.error("[avatar image save failed] cannot open temp file with path: "+tempFilePath);
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


    public Long getOwnerIdByImageId(Long imageId) throws LogicException{
        Long result = redisService.getOwnerUserIdByImageId(imageId);
        //cache miss, read from db
        if(result==null){
            Image image = imageMapper.selectByPrimaryKey(imageId);
            if(image==null){
                throw new LogicException(ErrorCode.IMAGE_NOT_FOUND, "Invalid image id");
            }
            result = image.getUserId();
            redisService.setImageIdToOwnerIdCache(imageId, result);
        }
        return result;
    }
}
