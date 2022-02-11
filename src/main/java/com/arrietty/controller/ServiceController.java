package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.entity.User;
import com.arrietty.service.ProfileServiceImpl;
import com.arrietty.service.RedisServiceImpl;
import com.arrietty.utils.response.Response;
import com.arrietty.utils.session.SessionContext;
import com.arrietty.utils.session.SessionIdGenerator;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.arrietty.consts.Api;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
public class ServiceController {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Autowired
    private ProfileServiceImpl profileService;



    @PostMapping(Api.DEBUG+"/login")
    public String postUserLogin(@RequestBody User user){
        String userSessionId = SessionIdGenerator.generate();
        redisServiceImpl.setUserSession(userSessionId, user);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        Cookie sessionCookie = new Cookie("userSessionId", userSessionId);
        response.addCookie(sessionCookie);
        return "login succeeds";
    }

    @Auth(authMode=)
    @GetMapping("/home")
    public String 



}
