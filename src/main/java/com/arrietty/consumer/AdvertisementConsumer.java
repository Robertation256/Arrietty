package com.arrietty.consumer;


import com.arrietty.consts.EventType;
import com.arrietty.entity.Advertisement;
import com.arrietty.entity.Course;
import com.arrietty.entity.OtherTag;
import com.arrietty.entity.TextbookTag;
import com.arrietty.pojo.AdvertisementEvent;
import com.arrietty.pojo.ESAdvertisementPO;
import com.arrietty.pojo.ESRelatedCoursePO;
import com.arrietty.pojo.ESTextbookTagPO;
import com.arrietty.service.CourseServiceImpl;
import com.arrietty.service.OtherTagServiceImpl;
import com.arrietty.service.TextbookTagServiceImpl;
import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RabbitListener(queues = "AdvertisementQueue")
public class AdvertisementConsumer {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private TextbookTagServiceImpl textbookTagService;

    @Autowired
    private CourseServiceImpl courseService;

    @Autowired
    private OtherTagServiceImpl otherTagService;

    @Autowired
    private RestHighLevelClient esClient;

    @RabbitHandler
    public void process(AdvertisementEvent event){

        if(event.getEventType().equals(EventType.ADVERTISEMENT_UPLOAD)){
            handleAdvertisementUpload(event.getAdvertisement());
        }
    }


    private void handleAdvertisementUpload(Advertisement advertisement){

        // 对数据做聚合
        ESAdvertisementPO esDocument = new ESAdvertisementPO();
        esDocument.setIsTextbook(advertisement.getIsTextbook());
        esDocument.setImageIds(advertisement.getImageIds());
        esDocument.setComment(advertisement.getComment());
        esDocument.setPrice(advertisement.getPrice());

        Instant instant = advertisement.getCreateTime().toInstant();
        LocalDateTime ldt = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
        esDocument.setCreateTime(ldt.format(fmt));


        if(advertisement.getIsTextbook()){
            TextbookTag textbookTag = textbookTagService.getTextbookTagById(advertisement.getTagId()).get(0);
            Course course = courseService.getCourseById(textbookTag.getCourseId()).get(0);
            ESTextbookTagPO esTextbookTagPO = new ESTextbookTagPO();
            esTextbookTagPO.setTitle(textbookTag.getTitle());
            esTextbookTagPO.setIsbn(textbookTag.getIsbn());
            esTextbookTagPO.setAuthor(textbookTag.getAuthor());
            esTextbookTagPO.setPublisher(textbookTag.getPublisher());
            esTextbookTagPO.setEdition(textbookTag.getEdition());
            esTextbookTagPO.setOriginalPrice(textbookTag.getOriginalPrice());

            ESRelatedCoursePO esRelatedCoursePO = new ESRelatedCoursePO();
            esRelatedCoursePO.setCourseCode(course.getCourseCode());
            esRelatedCoursePO.setCourseName(course.getCourseName());
            esRelatedCoursePO.setSubject(course.getSubject());

            esTextbookTagPO.setRelatedCourse(esRelatedCoursePO);
            esDocument.setTextbookTag(esTextbookTagPO);
        }
        else {
            OtherTag otherTag = otherTagService.getOtherTagById(advertisement.getTagId()).get(0);
            esDocument.setOtherTag(otherTag.getName());
        }

        IndexRequest indexRequest = new IndexRequest("advertisement","_doc");
        indexRequest.id(advertisement.getId().toString());
        String json = new Gson().toJson(esDocument);
        indexRequest.source(json, XContentType.JSON);
        indexRequest.opType("create");

        try{
            IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println("[RESPONSE]: "+indexResponse.toString());
        }
        catch (IOException e){
            //TODO: error log
            //e.printStackTrace();
        }



    }
}