package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.consts.RedisKey;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.TapMapper;
import com.arrietty.entity.Advertisement;
import com.arrietty.entity.Tap;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.TapEvent;
import com.arrietty.pojo.TapPO;
import com.arrietty.pojo.TapResponsePO;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TapServiceImpl {

    private static  final  String TAP_LOCK = "tap_lock";

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private TapMapper tapMapper;

    @Autowired
    private MQServiceImpl mqService;

    @Autowired
    private RedisServiceImpl redisService;

    // return whether a use has unread notification
    public Boolean hasNew(){
        Long userId = SessionContext.getUserId();
        Boolean result = redisService.getUserHasNewNotification(userId);
        if(result==null){
            result = tapMapper.getUserHasNewNotification(userId);
            redisService.setUserHasNewNotification(userId, result);
        }
        return result;
    }




    public TapResponsePO handleTap(Long id) throws LogicException {
        Set<String> tappedIds = getCurrentUserTappedAdIds();
        if(tappedIds.contains(id.toString())){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "The advertisement is already tapped.");
        }

        Advertisement advertisement = advertisementMapper.selectByPrimaryKey(id);
        if(advertisement==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Advertisement not found");
        }

        if(advertisement.getUserId().equals(SessionContext.getUserId())){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Cannot tap on your own advertisement");
        }

        TapEvent tapEvent = new TapEvent();
        tapEvent.setSenderId(SessionContext.getUserId());
        tapEvent.setReceiverId(advertisement.getUserId());
        tapEvent.setAdvertisementId(advertisement.getId());
        tapEvent.setCreateTime(new Date());
        mqService.pushTapEvent(tapEvent);

        ProfilePO profilePO = profileService.getUserProfile(advertisement.getUserId());
        TapResponsePO tapResponsePO = new TapResponsePO();
        tapResponsePO.setUsername(profilePO.getUsername());
        tapResponsePO.setNetId(profilePO.getNetId());
        tapResponsePO.setAvatarImageId(profilePO.getAvatarImageId());
        return tapResponsePO;
    }

    public Set<String> getCurrentUserTappedAdIds(){
        Set<String> ret = redisService.getUserTappedAdIds(SessionContext.getUserId());
        if(ret==null){
            List<Long> adIds = tapMapper.selectTappedAdIdsBySenderId(SessionContext.getUserId());
            ret = new HashSet<>();
            for(Long id: adIds){
                ret.add(id.toString());
            }
            redisService.addUserTappedAdIds(SessionContext.getUserId() ,ret);
        }

        return ret;
    }


    public List<TapPO> getCurrentUserNotifications(){
        Long userId = SessionContext.getUserId();
        List<Tap> taps = tapMapper.selectByReceiverId(userId);
        List<TapPO> result = new ArrayList<>(taps.size());
        for(Tap tap : taps){
            Advertisement advertisement = advertisementMapper.selectByPrimaryKey(tap.getAdId());
            if(advertisement==null){
                continue;
            }
            TapPO tapPO = new TapPO();
            tapPO.setId(tap.getId());
            ProfilePO profilePO = profileService.getUserProfile(tap.getSenderId());
            tapPO.setUsername(profilePO.getUsername());
            tapPO.setNetId(profilePO.getNetId());
            tapPO.setAvatarImageId(profilePO.getAvatarImageId());
            tapPO.setAdTitle(advertisement.getAdTitle());
            result.add(tapPO);

        }
        tapMapper.setUserNotificationAllRead(userId);
        redisService.setUserHasNewNotification(userId, false);

        return result;
    }

    public void incrementNumberOfTaps(Long adId){
        synchronized (TAP_LOCK){
            if(redisService.incrementNumberOfTaps(adId)==null){
                int tapNum = tapMapper.getNumberOfTapsByAdId(adId);
                redisService.setNumberOfTaps(adId, tapNum+1);
            }
        }

    }

    public Integer getNumberOfTaps(Long adId){
        synchronized (TAP_LOCK){
            Integer result = redisService.getNumberOfTaps(adId);
            if(result == null){
                result = tapMapper.getNumberOfTapsByAdId(adId);
                redisService.setNumberOfTaps(adId, result);
            }
            return result;
        }

    }
}
