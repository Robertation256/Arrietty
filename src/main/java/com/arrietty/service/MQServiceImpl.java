package com.arrietty.service;


import com.arrietty.pojo.AdvertisementEvent;
import com.arrietty.pojo.TapEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQServiceImpl {


    @Autowired
    private AmqpTemplate mqTemplate;


    public void pushAdEvent(AdvertisementEvent event){
        mqTemplate.convertAndSend("AdvertisementDirectExchange","AdvertisementDirectRouting", event);
    }

    public void pushTapEvent(TapEvent event){
        mqTemplate.convertAndSend("TapDirectExchange","TapDirectRouting", event);
    }
}
