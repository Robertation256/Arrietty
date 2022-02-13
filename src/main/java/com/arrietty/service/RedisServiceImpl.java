package com.arrietty.service;

import com.arrietty.consts.RedisKey;
import com.arrietty.entity.User;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.pojo.SessionPO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/28 10:56
 */

@Service
public class RedisServiceImpl {

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


}
