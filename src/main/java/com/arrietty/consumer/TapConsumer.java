package com.arrietty.consumer;

import com.arrietty.dao.TapMapper;
import com.arrietty.entity.Tap;
import com.arrietty.pojo.TapEvent;
import com.arrietty.service.RedisServiceImpl;
import com.arrietty.service.TapServiceImpl;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@RabbitListener(queues = "TapQueue")
public class TapConsumer {

    @Autowired
    private TapMapper tapMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private TapServiceImpl tapService;

    @RabbitHandler
    public void process(TapEvent event){
        // update user tapped ad id list, redis list

        redisService.addUserTappedAdId(event.getSenderId(), event.getAdvertisementId().toString());

        tapService.incrementNumberOfTaps(event.getAdvertisementId());

        Tap tap = new Tap();
        tap.setReceiverId(event.getReceiverId());
        tap.setSenderId(event.getSenderId());
        tap.setAdId(event.getAdvertisementId());
        tap.setCreateTime(event.getCreateTime());
        tap.setIsRead(false);
        tapMapper.insert(tap);
        redisService.setUserHasNewNotification(event.getReceiverId(),true);
    }
}
