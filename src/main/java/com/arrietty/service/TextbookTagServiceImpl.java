package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.TextbookTagMapper;
import com.arrietty.entity.OtherTag;
import com.arrietty.entity.TextbookTag;
import com.arrietty.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextbookTagServiceImpl {
    private static final String lock = "textbookTagWriteLock";

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private TextbookTagMapper textbookTagMapper;


    //TODO: 加上缓存机制, getTextbookTagById 和 getAllTags 区分
    public List<TextbookTag> getTextbookTagById(Long id){
        // id is null, return all TextbookTags
        if(id==null){
            return textbookTagMapper.selectAll();
        }
        else{
            List<TextbookTag> textbookTags = new ArrayList<>(1);
            TextbookTag textbookTag = textbookTagMapper.selectByPrimaryKey(id);
            if(textbookTag==null){
                throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Textbook does not exist.");
            }
            textbookTags.add(textbookTag);
            return textbookTags;
        }
    }

    public TextbookTag handleTextbookTagEdit(String action, TextbookTag textbookTag) throws LogicException {
        if("update".equals(action)){
            return handleTextbookTagUpdate(textbookTag);
        }
        else if("delete".equals(action)){
            handleTextbookTagDelete(textbookTag);
        }
        else {
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid action type.");
        }
        return null;
    }

    private TextbookTag handleTextbookTagUpdate(TextbookTag textbookTag) throws LogicException{

        if(textbookTag.getIsbn()==null || textbookTag.getTitle()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Textbook ISBN or title cannot be empty.");
        }

        // insert
        if(textbookTag.getId()==null){
            //check for duplicate textbook by isbn
            synchronized (lock){
                TextbookTag duplicateTextbookTag = textbookTagMapper.selectByIsbn(textbookTag.getIsbn());
                if(duplicateTextbookTag!=null){
                    throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Duplicate textbook tag exists.");
                }
                textbookTagMapper.insert(textbookTag);
                redisService.addToValidTextbookTagIdSet(textbookTag.getId());
                return textbookTag;
            }
        }
        //update
        else{
            synchronized (lock){
                if(!redisService.existsTextbookTagId(textbookTag.getId())){
                    throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Textbook tag does not exist.");
                }
                textbookTagMapper.updateByPrimaryKey(textbookTag);
            }

        }
        return null;
    }

    private void handleTextbookTagDelete(TextbookTag textbookTag) throws LogicException{
        if(textbookTag.getId()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Textbook tag id is empty.");
        }

        synchronized (lock){
            if(!redisService.existsTextbookTagId(textbookTag.getId())){
                throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Textbook tag does not exist.");
            }
            textbookTagMapper.deleteByPrimaryKey(textbookTag.getId());
            redisService.removeFromValidTextbookTagIdSet(textbookTag.getId());
        }

    }



}
