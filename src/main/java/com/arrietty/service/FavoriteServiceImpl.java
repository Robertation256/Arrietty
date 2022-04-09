package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.FavoriteMapper;
import com.arrietty.entity.Advertisement;
import com.arrietty.entity.Favorite;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.SearchResultItem;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private AdvertisementMapper advertisementMapper;

//    public List<SearchResultItem> handleGetFavorite(){
//        List<Favorite> favoriteList = favoriteMapper.selectByUserId(SessionContext.getUserId());
//        List<SearchResultItem> result = new ArrayList<>(favoriteList.size());
//
//
//
//    }


    public void handleMarkAction(String status, Long id) throws LogicException {
        if(id==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid id");
        }
        if("on".equals(status)){
            handleMark(id);
        }
        else if ("off".equals(status)){
            handleUnMark(id);
        }
        else {
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid status.");
        }


    }

    private void handleMark(Long id) throws LogicException{
        Advertisement advertisement = advertisementMapper.selectByPrimaryKey(id);
        if(advertisement==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Advertisement does not exist");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(SessionContext.getUserId());
        favorite.setAdId(id);

        if(redisService.isMarkedByCurrentUser(id)){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This advertisement has already been marked");
        }

        try{
            favoriteMapper.insert(favorite);
        }
        catch (Exception e){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Duplicate mark or advertisement does not exist");
        }

        redisService.addUserMarkedAdId(id);
    }

    private void handleUnMark(Long id){
        if(!redisService.isMarkedByCurrentUser(id)){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This advertisement is not marked");
        }

        favoriteMapper.deleteByUserIdAndAdId(SessionContext.getUserId(), id);
        redisService.removeUserMarkedAdId(id);
    }

//    private SearchResultItem getSearchResultItem(Long adId){
//        Advertisement advertisement = advertisementMapper.selectByPrimaryKey(adId);
//        SearchResultItem po = new SearchResultItem();
//        po.setId(advertisement.getId());
//        po.setImageIds(advertisement.getImageIds());
//        po.setCreateTime(advertisement.getCreateTime());
//    }
}
