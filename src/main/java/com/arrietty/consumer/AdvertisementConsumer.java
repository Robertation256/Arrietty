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
import com.arrietty.service.RedisServiceImpl;
import com.arrietty.service.TextbookTagServiceImpl;
import com.google.gson.Gson;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
@RabbitListener(queues = "AdvertisementQueue")
public class AdvertisementConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementConsumer.class);

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private TextbookTagServiceImpl textbookTagService;

    @Autowired
    private CourseServiceImpl courseService;

    @Autowired
    private OtherTagServiceImpl otherTagService;

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private RedisServiceImpl redisService;

    @RabbitHandler
    public void process(AdvertisementEvent event){

        if(event.getEventType().equals(EventType.ADVERTISEMENT_UPLOAD)){
            handleAdvertisementUpload(event.getAdvertisement());
        }
        else if (event.getEventType().equals(EventType.ADVERTISEMENT_UPDATE)){
            handleAdvertisementUpdate(event.getAdvertisement());
        }
        else if(event.getEventType().equals(EventType.ADVERTISEMENT_DELETE)){
            handleAdvertisementDelete(event.getAdvertisement());
        }

    }


    private void handleAdvertisementUpload(Advertisement advertisement){
        redisService.incrementUserAdUploadNum();


        ESAdvertisementPO esDocument = new ESAdvertisementPO();
        esDocument.setUserId(advertisement.getUserId());
        esDocument.setAdTitle(advertisement.getAdTitle());
        esDocument.setIsTextbook(advertisement.getIsTextbook());
        esDocument.setImageIds(advertisement.getImageIds());
        esDocument.setComment(advertisement.getComment());
        esDocument.setPrice(advertisement.getPrice());

        Instant instant = advertisement.getCreateTime().toInstant();
        LocalDateTime ldt = instant.atZone(ZoneOffset.systemDefault()).toLocalDateTime();
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
            esDocument.setSuggest("T:"+textbookTag.getTitle());
            esDocument.setTextbookTag(esTextbookTagPO);
        }
        else {
            if (advertisement.getTagId()!=null){
                List<OtherTag> otherTags = otherTagService.getOtherTagById(advertisement.getTagId());
                if (otherTags.size()>0){
                    esDocument.setOtherTag(otherTags.get(0).getName());
                }
            }

            esDocument.setSuggest("O:"+advertisement.getAdTitle());

        }

        IndexRequest indexRequest = new IndexRequest("advertisement","_doc");
        indexRequest.id(advertisement.getId().toString());
        String json = new Gson().toJson(esDocument);
        indexRequest.source(json, XContentType.JSON);
        indexRequest.opType("create");

        try{
            IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
            redisService.setAdvertisementTimestamp(new Date());
            System.out.println("[RESPONSE]: "+indexResponse.toString());
        }
        catch (IOException e){
            logger.error("[failed to insert to ES]", e);
        }
        
    }

    private void handleAdvertisementUpdate(Advertisement advertisement){
        redisService.incrementUserAdUpdateNum();

        ESAdvertisementPO esDocument = new ESAdvertisementPO();
        esDocument.setImageIds(advertisement.getImageIds());
        esDocument.setComment(advertisement.getComment());
        esDocument.setPrice(advertisement.getPrice());

        Instant instant = advertisement.getCreateTime().toInstant();
        LocalDateTime ldt = instant.atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        esDocument.setCreateTime(ldt.format(fmt));

        UpdateRequest updateRequest = new UpdateRequest("advertisement","_doc",advertisement.getId().toString());
        updateRequest.doc(new Gson().toJson(esDocument), XContentType.JSON);
        try{
            esClient.update(updateRequest);
        }
        catch (IOException e){
            logger.error("[failed to update ES]", e);
        }
    }

    private void handleAdvertisementDelete(Advertisement advertisement){
        redisService.incrementUserAdDeleteNum();

        DeleteRequest deleteRequest = new DeleteRequest("advertisement","_doc",advertisement.getId().toString());
        try{
            esClient.delete(deleteRequest);
        }
        catch (IOException e){
            logger.error("[failed to delete from ES]", e);
        }

        redisService.adDeleteCleanUp(advertisement.getId());
    }
}
