package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
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




    public TapResponsePO handleTap(Long id) throws LogicException {
        Set<Long> tappedIds = getCurrentUserTappedAdIds();
        if(tappedIds.contains(id)){
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

    public Set<Long> getCurrentUserTappedAdIds(){
        Set<Long> ret = redisService.getUserTappedAdIds(SessionContext.getUserId());
        if(ret==null){
            List<Long> adIds = tapMapper.selectTappedAdIdsBySenderId(SessionContext.getUserId());
            ret = new HashSet<>(adIds);
            redisService.addUserTappedAdIds(SessionContext.getUserId() ,adIds);
        }

        return ret;
    }


    public List<TapPO> getCurrentUserNotifications(){
        List<Tap> taps = tapMapper.selectByReceiverId(SessionContext.getUserId());
        List<TapPO> result = new ArrayList<>(taps.size());
        for(Tap tap : taps){
            TapPO tapPO = new TapPO();
            tapPO.setId(tap.getId());
            ProfilePO profilePO = profileService.getUserProfile(tap.getSenderId());
            tapPO.setUsername(profilePO.getUsername());
            tapPO.setNetId(profilePO.getNetId());
            tapPO.setAvatarImageId(profilePO.getAvatarImageId());

            //TODO: add caching for ad
            Advertisement advertisement = advertisementMapper.selectByPrimaryKey(tap.getAdId());
            tapPO.setAdTitle(advertisement.getAdTitle());
            result.add(tapPO);
        }

        return result;
    }
}
