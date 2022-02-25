package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.OtherTagMapper;
import com.arrietty.entity.Course;
import com.arrietty.entity.OtherTag;
import com.arrietty.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtherTagServiceImpl {
    private static final String lock = "otherTagWriteLock";
    
    @Autowired
    private OtherTagMapper otherTagMapper;

    public List<OtherTag> getOtherTagById(Long id){

        // id is null, return all otherTags
        if(id==null){
            return otherTagMapper.selectAll();
        }
        else{
            List<OtherTag> otherTags = new ArrayList<>(1);
            OtherTag otherTag = otherTagMapper.selectByPrimaryKey(id);
            if(otherTag==null){
                throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Tag does not exist.");
            }
            otherTags.add(otherTag);
            return otherTags;
        }
    }


    public OtherTag handleOtherTagEdit(String action, OtherTag otherTag) throws LogicException {
        if("update".equals(action)){
            return handleOtherTagUpdate(otherTag);
        }
        else if("delete".equals(action)){
            handleOtherTagDelete(otherTag);
        }
        else {
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid action type.");
        }
        return null;
    }

    private OtherTag handleOtherTagUpdate(OtherTag otherTag) throws LogicException{

        if(otherTag.getName()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Tag name cannot be empty.");
        }

        // insert
        if(otherTag.getId()==null){
            //check for duplicate otherTag by otherTagCode
            synchronized (lock){
                OtherTag duplicateOtherTag = otherTagMapper.selectByName(otherTag.getName());
                if(duplicateOtherTag!=null){
                    throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Duplicate tag exists.");
                }
                otherTagMapper.insert(otherTag);
                return otherTag;
            }
        }
        //update
        else{
            int recordUpdatedAmount = otherTagMapper.updateByPrimaryKey(otherTag);
            if(recordUpdatedAmount==0){
                throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Tag does not exist.");
            }
        }
        return null;
    }

    private void handleOtherTagDelete(OtherTag otherTag) throws LogicException{
        if(otherTag.getId()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Tag id is empty.");
        }
        int recordDeletedAmount = otherTagMapper.deleteByPrimaryKey(otherTag.getId());
        if(recordDeletedAmount==0){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Tag does not exist.");
        }
    }
}
