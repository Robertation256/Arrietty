package com.arrietty.service.redis;

import com.arrietty.consts.RedisKey;
import com.arrietty.entity.User;
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

    public void setUserSession(User user){
        Gson gson = new Gson();
        String serialized = gson.toJson(user, User.class);
        redisTemplate.opsForValue().set(RedisKey.USER_SESSION+user.getUserId().toString(),serialized);
    }

    public User getUserSession(Long userId){
        Object obj = redisTemplate.opsForValue().get(RedisKey.USER_SESSION+userId.toString());
        if (obj == null) return null;
        String jsonString = (String) obj;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, User.class);
    }


}
