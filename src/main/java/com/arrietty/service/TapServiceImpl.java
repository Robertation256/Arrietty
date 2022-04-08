package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.TapMapper;
import com.arrietty.entity.Advertisement;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.TapEvent;
import com.arrietty.pojo.TapResponsePO;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
