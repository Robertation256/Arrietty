package com.arrietty.service.file;

import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.ImageMapper;
import com.arrietty.entity.Image;
import com.arrietty.exception.LogicException;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
public class AvatarStorageService implements FileStorageService{

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg","jpeg","png");

    @Autowired
    private ImageMapper imageMapper;

    @Value("${file.avatar-image-path}")
    private String BASE_PATH;

    @Override
    public void init() {

        Path avatarFolderPath = Paths.get(BASE_PATH);

        if (!Files.exists(avatarFolderPath)){
            try{
                Files.createDirectory(Paths.get(BASE_PATH));
            }
            catch (IOException e) {
                throw new RuntimeException("Cannot create folder for avatar images.");
            }
        }
    }

    @Override
    public String save(MultipartFile file) {
        String imageFormat = checkImageFormat(file);
        String externalImageId = UUID.randomUUID().toString().replace("-","");

        Image originalAvatar = imageMapper.queryByUploadUserId(SessionContext.getUserId());
        // insert when user avatar is not found
        if (originalAvatar==null){
            Image image = new Image();
            image.setExternalImageId(externalImageId);
            image.setImageFormat(imageFormat);
            image.setImageType("avatar");
            image.setUploadUserId(SessionContext.getUserId());
            try {
                file.transferTo(new File(BASE_PATH + externalImageId +
                        "." + imageFormat));
                imageMapper.insert(image);
                return externalImageId;
            }
            catch (IOException e){
                throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "Save image failed");
            }
        }
        // overwrite when user avatar is found
        else {
            try {
                file.transferTo(new File(BASE_PATH + originalAvatar.getExternalImageId() +
                        "." + originalAvatar.getImageFormat()));
                imageMapper.updateByPrimaryKey(originalAvatar);
                return originalAvatar.getExternalImageId();
            }
            catch (IOException e){
                throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "Save image failed");
            }

        }
    }

    @Override
    public Resource load(String externalImageId) {
        if (externalImageId == null || externalImageId.length()<32){
            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Bad image id");
        }

        Image image = imageMapper.queryByExternalImageId(externalImageId);
        if (image == null){
            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Image not found");
        }

        Path imagePath = Paths.get(BASE_PATH + image.getExternalImageId() + "." + image.getImageFormat());
        try {
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() || resource.isReadable()){
                return resource;
            }
        }
        catch ( MalformedURLException e) {
            throw new LogicException(ErrorCode.IMAGE_LOAD_ERROR, "Bad directory");
        }
        return null;
    }

    @Override
    public void delete(List<String> externalImageIds) {
        //TO-DO
    }

    private String checkImageFormat(MultipartFile file){
        if (file == null || file.getOriginalFilename() == null){
            throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "File is empty.");
        }

        // check image format
        String[] splitFilename = file.getOriginalFilename().split("\\.");
        String imageFormat = splitFilename[splitFilename.length-1];
        if (!ALLOWED_IMAGE_TYPES.contains(imageFormat)){
            throw new LogicException(ErrorCode.IMAGE_UPLOAD_ERROR, "Invalid image type.");
        }

        return imageFormat;
    }
}
