package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.BulletinMapper;
import com.arrietty.entity.Bulletin;
import com.arrietty.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BulletinServiceImpl {

    public static final String BULLETIN_LOCK = "bulletin_lock";

    @Autowired
    private BulletinMapper bulletinMapper;

    @Autowired
    private RedisServiceImpl redisService;


    public List<Bulletin> getBulletin(){
        List<Bulletin> bulletins = redisService.getBulletin();

        //cache miss, read from mysql
        if(bulletins==null ){
            bulletins = bulletinMapper.selectAll();
            redisService.setBulletin(bulletins);
        }

        return bulletins;
    }

    public Bulletin handlePostBulletin(String action, Bulletin bulletin){
        if("update".equals(action)){
            if(bulletin.getTitle()==null ||
                    bulletin.getTitle().length()==0 ||
                    bulletin.getContent()==null ||
                    bulletin.getContent().length()==0
            ){
                throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Title and content cannot be empty.");
            }

            // insert
            if (bulletin.getId()==null){
                synchronized (BULLETIN_LOCK){
                    Date date = new Date();
                    bulletin.setCreateTime(date);
                    bulletinMapper.insert(bulletin);
                    List<Bulletin> bulletins = redisService.getBulletin();
                    bulletins.add(bulletin);
                    redisService.setBulletin(bulletins);
                }
            }
            //update
            else {
                synchronized (BULLETIN_LOCK){
                    Date date = new Date();
                    bulletin.setCreateTime(date);
                    if(bulletinMapper.updateByPrimaryKey(bulletin)==0){
                        throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This bulletin does not exist");
                    }
                    List<Bulletin> bulletins = redisService.getBulletin();
                    for(int i=0; i<bulletins.size(); i++){
                        if(bulletins.get(i).getId().equals(bulletin.getId())){
                            bulletins.set(i,bulletin);
                            break;
                        }
                    }
                    redisService.setBulletin(bulletins);
                }
            }
            return bulletin;
        }
        else if ("delete".equals(action)){
            synchronized (BULLETIN_LOCK){
                if(bulletin.getId()==null || bulletinMapper.deleteByPrimaryKey(bulletin.getId())==0){
                    throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "This bulletin does not exist");
                }

                List<Bulletin> bulletins = redisService.getBulletin();
                for(int i=0; i<bulletins.size(); i++){
                    if(bulletins.get(i).getId().equals(bulletin.getId())){
                        bulletins.remove(i);
                        break;
                    }
                }
                redisService.setBulletin(bulletins);

            }
            return null;
        }
        else {
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Invalid action");
        }
    }

}
