package com.arrietty.controller;

import com.arrietty.entity.User;
import com.arrietty.service.redis.RedisServiceImpl;
import com.arrietty.utils.response.Response;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.arrietty.consts.Api;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    // Debugging APIs for setting user sessions
    @RequestMapping(Api.DEBUG+"/session")
    public String getUserName(@RequestParam("userId") Long userId){
        User user = redisServiceImpl.getUserSession(userId);
        return user.getFirstName()+", "+user.getLastName();
    }

    @PostMapping(Api.DEBUG+"/session")
    public String postUserSession(@RequestBody User user){
        redisServiceImpl.setUserSession(user);
        return "success";
    }


    @RequestMapping("/set")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value) {
        redisServiceImpl.set(key,value);
        return "operation succeeds";
    }

    @RequestMapping("/get")
    public String get(@RequestParam("key") String key) {
        String result = (String) redisServiceImpl.get(key);
        Response<String> response = Response.buildSuccessResponse(String.class, result);
        Gson gson = new Gson();
        String res = gson.toJson(response);
        return res;
    }








}
