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

    @RabbitHandler
    public void process(TapEvent event){
        // 更新user tapped ad id list, redis list

        redisService.addUserTappedAdId(event.getSenderId(), event.getAdvertisementId().toString());

        //数据库持久化
        Tap tap = new Tap();
        tap.setReceiverId(event.getReceiverId());
        tap.setSenderId(event.getSenderId());
        tap.setAdId(event.getAdvertisementId());
        tap.setCreateTime(event.getCreateTime());
        tapMapper.insert(tap);
    }
}
