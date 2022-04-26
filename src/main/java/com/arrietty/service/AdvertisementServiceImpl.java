package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.consts.EventType;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.ImageMapper;
import com.arrietty.entity.Advertisement;
import com.arrietty.entity.Image;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.*;
import com.arrietty.utils.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdvertisementServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementServiceImpl.class);

    @Value("${file.max-image-num-per-ad}")
    private int MAX_IMAGE_NUM;


    private static final BigDecimal MIN_PRICE = new BigDecimal(0);
    private static final BigDecimal MAX_PRICE = new BigDecimal(10000);

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private TextbookTagServiceImpl textbookTagService;


    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private MQServiceImpl mqService;

    @Autowired
    private RedisServiceImpl redisService;


    public Advertisement getAdvertisementById(Long id){
        //TODO caching?
        return advertisementMapper.selectByPrimaryKey(id);
    }

    public boolean isCurrentUserAd(Long adId){
        Advertisement advertisement = getAdvertisementById(adId);
        if(advertisement==null ||
                advertisement.getUserId()==null ||
                !advertisement.getUserId().equals(SessionContext.getUserId())
        ){
            return false;
        }
        return true;
    }

    public Date getLastModified(){
        Date result = redisService.getAdvertisementTimestamp();
        if(result==null){
            result = advertisementMapper.getLatestAdCreateTime();
            redisService.setAdvertisementTimestamp(result);
        }
        return result;
    }


    public AdvertisementResponsePO handlePostAdvertisement(String action, PostAdvertisementRequestPO requestPO) throws LogicException {
        if("add".equals(action)){
            checkAdvertisementFormat(requestPO);
            return insertAdvertisement(requestPO);
        }
        else if("update".equals(action)){
            return updateAdvertisement(requestPO);
        }
        else if ("delete".equals(action)){
            deleteAdvertisement(requestPO);
            return null;
        }
        throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid action type.");
    }



    @Transactional(rollbackFor = Exception.class)
    public AdvertisementResponsePO insertAdvertisement(PostAdvertisementRequestPO requestPO) throws LogicException{
        List<String> imageIds = new ArrayList<>(requestPO.getImages().size());
        for(MultipartFile imageFile: requestPO.getImages()){
            Long imageId = imageService.insertAdvertisementImage(imageFile);
            imageIds.add(imageId.toString());
        }

        Date date = new Date();
        Advertisement advertisement = new Advertisement();
        advertisement.setUserId(SessionContext.getUserId());
        advertisement.setAdTitle(requestPO.getAdTitle());
        advertisement.setIsTextbook(requestPO.getIsTextbook());
        advertisement.setTagId(requestPO.getTagId());
        advertisement.setImageIds(String.join( ",", imageIds));
        advertisement.setComment(requestPO.getComment());
        advertisement.setPrice(requestPO.getPrice());
        advertisement.setNumberOfTaps(0);
        advertisement.setCreateTime(date);

        try{
            advertisementMapper.insert(advertisement);
        }
        catch (Exception e){
            // clean up previously inserted images from file system
            imageService.deleteImageFiles(imageIds);
            logger.error("[advertisement upload failed]", e);
            throw new LogicException(ErrorCode.ADVERTISEMENT_SAVE_ERROR, "Advertisement upload failed");
        }

        // push ad upload event to message queue for more further processing
        AdvertisementEvent advertisementEvent = new AdvertisementEvent();
        advertisementEvent.setAdvertisement(advertisement);
        advertisementEvent.setEventType(EventType.ADVERTISEMENT_UPLOAD);
        advertisementEvent.setTimestamp(date);
        mqService.pushAdEvent(advertisementEvent);

        return null;
    }



    @Transactional(rollbackFor = Exception.class)
    public AdvertisementResponsePO updateAdvertisement(PostAdvertisementRequestPO requestPO){
        if(
                requestPO.getPrice()==null ||
                        (requestPO.getPrice().compareTo(MIN_PRICE)<0 ||
                                requestPO.getPrice().compareTo(MAX_PRICE)>0)

        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Bad form format.");
        }

        Advertisement ad = getAdvertisementById(requestPO.getId());

        if(ad==null ||
                !ad.getUserId().equals(SessionContext.getUserId())
        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Advertisement does not exist.");
        }

        // 新照片全增
        List<String> imageIds = new ArrayList<>();
        if(requestPO.getImages()!=null){
            for(MultipartFile imageFile: requestPO.getImages()){
                // TODO: 单张insert 改为batch insert
                Long imageId = imageService.insertAdvertisementImage(imageFile);
                imageIds.add(imageId.toString());
            }
        }


        //旧照片全删
        if(ad.getImageIds()!=null && ad.getImageIds().length()>0){
            String[] tmp = ad.getImageIds().split(",");
            for(String strId: tmp){
                Long id = Long.parseLong(strId);
                imageService.deleteImage(id);
            }
        }



        Date date = new Date();
        ad.setCreateTime(date);
        ad.setImageIds(String.join(",", imageIds));
        ad.setPrice(requestPO.getPrice());
        ad.setComment(requestPO.getComment());


        int count = advertisementMapper.updateByPrimaryKey(ad);
        if(count==0){
            throw new LogicException(ErrorCode.ADVERTISEMENT_SAVE_ERROR, "Update to DB failed.");
        }

        AdvertisementEvent advertisementEvent = new AdvertisementEvent();
        advertisementEvent.setEventType(EventType.ADVERTISEMENT_UPDATE);
        advertisementEvent.setAdvertisement(ad);
        mqService.pushAdEvent(advertisementEvent);

        AdvertisementResponsePO responsePO = new AdvertisementResponsePO();
        responsePO.setId(ad.getId());
        responsePO.setIsTextbook(ad.getIsTextbook());
        responsePO.setTagId(ad.getTagId());
        responsePO.setImageIds(ad.getImageIds());
        responsePO.setComment(ad.getComment());
        responsePO.setPrice(ad.getPrice());
        responsePO.setNumberOfTaps(ad.getNumberOfTaps());
        responsePO.setCreateTime(date);

        return responsePO;

    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAdvertisement(PostAdvertisementRequestPO requestPO) throws  LogicException {
        if(requestPO.getId()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Bad form format.");
        }
        Advertisement ad = getAdvertisementById(requestPO.getId());
        if(ad==null ||
                !ad.getUserId().equals(SessionContext.getUserId())
        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Advertisement does not exist.");
        }

        if(ad.getImageIds()!=null && ad.getImageIds().length()>0){
            String[] tmp = ad.getImageIds().split(",");
            for(String strId: tmp){
                Long id = Long.parseLong(strId);
                imageService.deleteImage(id);
            }
        }

        advertisementMapper.deleteByPrimaryKey(requestPO.getId());

        AdvertisementEvent event = new AdvertisementEvent();
        event.setEventType(EventType.ADVERTISEMENT_DELETE);
        event.setAdvertisement(ad);
        mqService.pushAdEvent(event);

    }


    private void checkAdvertisementFormat(PostAdvertisementRequestPO requestPO) throws LogicException{
        if(requestPO==null ||
                requestPO.getAdTitle() == null ||
                requestPO.getAdTitle().length()==0 ||
                requestPO.getAdTitle().length()>255 ||
                requestPO.getImages()==null ||
                requestPO.getImages().size()>MAX_IMAGE_NUM ||
                requestPO.getIsTextbook()==null ||
                (requestPO.getIsTextbook() && requestPO.getTagId()==null) ||
                requestPO.getPrice()==null ||
                requestPO.getPrice().compareTo(MIN_PRICE)<0 ||
                requestPO.getPrice().compareTo(MAX_PRICE)>0
        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Bad form format.");
        }


        // if the advertisement is about a textbook, it must come with a valid tag id
        if (
                requestPO.getIsTextbook() &&
                        !redisService.existsTextbookTagId(requestPO.getTagId())
        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Tag id does not exist.");
        }
    }

}
