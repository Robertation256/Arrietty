package com.arrietty.service;

import com.arrietty.consts.RedisKey;
import com.arrietty.entity.Bulletin;
import com.arrietty.entity.User;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.SessionPO;
import com.arrietty.utils.session.SessionContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/28 10:56
 */

@Service
public class RedisServiceImpl {

    private static final Type LIST_OF_BULLETIN_TYPE = new TypeToken<ArrayList<Bulletin>>() {}.getType();

    @Autowired
    private RedisTemplate redisTemplate;

    public void init(){
        // 重启清空redis
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPLOAD_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPDATE_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_DELETE_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_MARK_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_UNMARK_NUM, 0);
        redisTemplate.opsForValue().set(RedisKey.USER_SEARCH_NUM, 0);
        
    }



    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key){

        return redisTemplate.opsForValue().get(key);
    }

    public void setUserSession(String userSessionId, SessionPO sessionPO){
        Gson gson = new Gson();
        String serialized = gson.toJson(sessionPO, SessionPO.class);
        redisTemplate.opsForValue().set(RedisKey.USER_SESSION+userSessionId,serialized);
    }

    public SessionPO getUserSession(String  userSessionId){
        Object obj = redisTemplate.opsForValue().get(RedisKey.USER_SESSION+userSessionId);
        if (obj == null) return null;
        String jsonString = (String) obj;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, SessionPO.class);
    }

    public void setUserProfile(Long userId, ProfilePO profile){
        if (userId==null) return;

        String serialized = new Gson().toJson(profile, ProfilePO.class);
        redisTemplate.opsForValue().set(RedisKey.USER_PROFILE+userId.toString(),serialized);
    }

    public ProfilePO getUserProfile(Long  userId){
        if(userId==null) return null;

        Object obj = redisTemplate.opsForValue().get(RedisKey.USER_PROFILE+userId.toString());
        if (obj == null) return null;
        String jsonString = (String) obj;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ProfilePO.class);
    }

    public List<Long> getAllTextbookTagIds(){
        Object obj = redisTemplate.opsForValue().get(RedisKey.ALL_TEXTBOOK_TAG_ID);
        if (obj == null) return null;
        String str = (String) obj;
        String[] ids = str.split(",");
        List<Long> ret = new ArrayList<>(ids.length);
        for(String id:ids){
            ret.add(Long.parseLong(id));
        }
        return ret;
    }

    public void setAllTextbookTagIds(String val){
        redisTemplate.opsForValue().set(RedisKey.ALL_TEXTBOOK_TAG_ID,val);
    }


    public void addUserTappedAdId(Long userId, String adId){
        redisTemplate.opsForSet().add(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+ userId.toString(),adId);

    }

    public void addUserTappedAdIds(Long userId, Set<String> adIds){
        redisTemplate.opsForSet().union(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+ userId.toString(),adIds);
    }

    public Set<String> getUserTappedAdIds(Long userId){
        Set<String> ret = redisTemplate.opsForSet().members(RedisKey.CURRENT_USER_TAPPED_AD_ID_LIST+ userId.toString());
        if(ret==null){
            return new HashSet<>();
        }
        return ret;
    }

    public Long incrementNumberOfTaps(Long adId){
        return redisTemplate.opsForValue().increment(RedisKey.NUMBER_OF_TAPS+adId.toString(), 1L);
    }

    public void setNumberOfTaps(Long adId, Integer num){
        redisTemplate.opsForValue().set(RedisKey.NUMBER_OF_TAPS+adId.toString(), num);
    }

    public Integer getNumberOfTaps(Long adId){
        return (Integer) redisTemplate.opsForValue().get(RedisKey.NUMBER_OF_TAPS);
    }


    public Date getAdvertisementTimestamp(){
        String json = (String) redisTemplate.opsForValue().get(RedisKey.AD_TIMESTAMP);
        if(json!=null){
            return new Gson().fromJson(json, Date.class);
        }
        return null;
    }

    public void setAdvertisementTimestamp(Date timestamp){
        redisTemplate.opsForValue().set(RedisKey.AD_TIMESTAMP, new Gson().toJson(timestamp));
    }

    public Boolean getUserHasNewNotification(Long userId){
        return (Boolean) redisTemplate.opsForValue().get(RedisKey.USER_NOTIFICATION_HAS_NEW+userId.toString());
    }

    public void setUserHasNewNotification(Long userId, boolean hasNew){
        redisTemplate.opsForValue().set(RedisKey.USER_NOTIFICATION_HAS_NEW+userId.toString(), hasNew);
    }



    public Set<String> getCurrentUserMarkedAdIds(){
        return redisTemplate.opsForSet().members(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString());
    }

    public boolean isMarkedByCurrentUser(String adId){
        return redisTemplate.opsForSet().isMember(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString(), adId);
    }

    public void addUserMarkedAdId(String adId){
        redisTemplate.opsForSet().add(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString(), adId);
    }

    public void addUserMarkedAdIds(Set<String> adIds){
        redisTemplate.opsForSet().union(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString(), adIds);
    }

    public void removeUserMarkedAdId(String adId){
        redisTemplate.opsForSet().remove(RedisKey.CURRENT_USER_MARKED_AD_ID_LIST+SessionContext.getUserId().toString(), adId);
    }

    public List<Bulletin> getBulletin(){
        String json = (String) redisTemplate.opsForValue().get(RedisKey.BULLETIN_CACHE);
        if(json==null){
            return null;
        }
        return new Gson().fromJson(json, LIST_OF_BULLETIN_TYPE);
    }

    public void setBulletin(List<Bulletin> bulletins){
        String json = new Gson().toJson(bulletins);
        redisTemplate.opsForValue().set(RedisKey.BULLETIN_CACHE, json);
    }


    public void incrementUserAdUploadNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_UPLOAD_NUM,1);
    }

    public int getUserAdUploadNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_UPLOAD_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPLOAD_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserAdUpdateNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_UPDATE_NUM,1);
    }

    public int getUserAdUpdateNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_UPDATE_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_UPDATE_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserAdDeleteNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_AD_DELETE_NUM,1);
    }

    public int getUserAdDeleteNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_AD_DELETE_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_AD_DELETE_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserMarkNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_MARK_NUM,1);
    }

    public int getUsermarkNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_MARK_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_MARK_NUM, 0);
        return result==null?0:result;
    }

    public void incrementUserUnmarkNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_UNMARK_NUM,1);
    }

    public int getUserUnmarkNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_UNMARK_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_UNMARK_NUM, 0);
        return result==null?0:result;
    }

    public void incrementSearchRequestNum(){
        redisTemplate.opsForValue().increment(RedisKey.USER_SEARCH_NUM,1);
    }

    public int getSearchRequestNum(){
        Integer result = (Integer) redisTemplate.opsForValue().get(RedisKey.USER_SEARCH_NUM);
        redisTemplate.opsForValue().set(RedisKey.USER_SEARCH_NUM, 0);
        return result==null?0:result;
    }

}
