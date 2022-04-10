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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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



    public Integer getVersionId(String target){
        if("advertisement".equals(target)){
            return (Integer) redisTemplate.opsForValue().get(RedisKey.AD_VERSION_ID);
        }

        return null;
    }

    public void incrementVersionId(String target){
        long res = 0L;
        if("advertisement".equals(target)){
            res = redisTemplate.opsForValue().increment(RedisKey.AD_VERSION_ID);
        }

        // set version id back to zero when there is an overflow
        if((int) res<0){
            redisTemplate.opsForValue().set(RedisKey.AD_VERSION_ID,0);
        }
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

}
