package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.FavoriteMapper;
import com.arrietty.entity.Advertisement;
import com.arrietty.entity.Favorite;
import com.arrietty.entity.OtherTag;
import com.arrietty.entity.TextbookTag;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.SearchResultItem;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteServiceImpl {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private TextbookTagServiceImpl textbookTagService;

    @Autowired
    private OtherTagServiceImpl otherTagService;

    @Autowired
    private TapServiceImpl tapService;

    @Autowired
    private ProfileServiceImpl profileService;

    public Set<String> getCurrentUserMarkedAdIds(){
        Set<String> result = redisService.getCurrentUserMarkedAdIds();
        if(result==null){
            List<Favorite> favoriteList = favoriteMapper.selectByUserId(SessionContext.getUserId());
            result = new HashSet<>();
            for (Favorite favorite : favoriteList){
                result.add(favorite.getAdId().toString());
            }
            redisService.addUserMarkedAdIds(result);
        }
        return result;
    }


    public List<SearchResultItem> handleGetFavorite(){
        Set<String> markedAdIds = getCurrentUserMarkedAdIds();
        List<SearchResultItem> result = new ArrayList<>(markedAdIds.size());
        for (String id: markedAdIds){
            result.add(getSearchResultItem(Long.parseLong(id)));
        }
        return result;
    }


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

        if(redisService.isMarkedByCurrentUser(id.toString())){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This advertisement has already been marked");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(SessionContext.getUserId());
        favorite.setAdId(id);
        try{
            favoriteMapper.insert(favorite);
        }
        catch (Exception e){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Duplicate mark or advertisement does not exist");
        }

        redisService.incrementUserMarkNum();
        redisService.addUserMarkedAdId(id.toString());
    }

    private void handleUnMark(Long id){
        if(!redisService.isMarkedByCurrentUser(id.toString())){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "This advertisement is not marked");
        }

        favoriteMapper.deleteByUserIdAndAdId(SessionContext.getUserId(), id);
        redisService.removeUserMarkedAdId(id.toString());
        redisService.incrementUserUnmarkNum();
    }

    private SearchResultItem getSearchResultItem(Long adId){
        Advertisement advertisement = advertisementMapper.selectByPrimaryKey(adId);
        SearchResultItem po = new SearchResultItem();
        po.setId(advertisement.getId());
        po.setImageIds(advertisement.getImageIds());
        po.setCreateTime(advertisement.getCreateTime());
        po.setPrice(advertisement.getPrice());
        po.setComment(advertisement.getComment());
        po.setAdTitle(advertisement.getAdTitle());
        po.setIsMarked(true);
        po.setNumberOfTaps(tapService.getNumberOfTaps(adId));

        if(advertisement.getIsTextbook()){
            TextbookTag textbookTag = textbookTagService.getTextbookTagById(advertisement.getTagId()).get(0);
            po.setTextbookTitle(textbookTag.getTitle());
            po.setOriginalPrice(textbookTag.getOriginalPrice());
            po.setEdition(textbookTag.getEdition());
            po.setAuthor(textbookTag.getAuthor());
            po.setPublisher(textbookTag.getPublisher());
            po.setAdType("textbook");
        }
        else{
            OtherTag otherTag = otherTagService.getOtherTagById(advertisement.getTagId()).get(0);
            po.setOtherTag(otherTag.getName());
            po.setAdType("other");
        }

        Set<String> tappedAdIds = tapService.getCurrentUserTappedAdIds();
        if(tappedAdIds.contains(advertisement.getId().toString())){
            ProfilePO profilePO = profileService.getUserProfile(advertisement.getUserId());
            po.setUsername(profilePO.getUsername());
            po.setUserNetId(profilePO.getNetId());
            po.setUserAvatarImageId(profilePO.getAvatarImageId());
        }

        return po;
    }
}
