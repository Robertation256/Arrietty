package com.arrietty.controller;

import com.arrietty.service.impl.RedisTestService;
import com.arrietty.utils.response.Response;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.arrietty.consts.Api;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private RedisTestService redisTestService;

    // profile APIs
    @RequestMapping(Api.PROFILE)
    public String getProfile(String netId){
        return "Hello World";
    }


    @RequestMapping("/set")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value) {
        redisTestService.set(key,value);
        return "operation succeeds";
    }

    @RequestMapping("/get")
    public String get(@RequestParam("key") String key) {
        String result = (String) redisTestService.get(key);
        Response<String> response = Response.buildSuccessResponse(String.class, result);
        Gson gson = new Gson();
        String res = gson.toJson(response);
        return res;
    }





}
