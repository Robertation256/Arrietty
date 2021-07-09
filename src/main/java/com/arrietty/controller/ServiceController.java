package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.entity.Profile;
import com.arrietty.entity.User;
import com.arrietty.service.ProfileServiceImpl;
import com.arrietty.service.redis.RedisServiceImpl;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping("/serviceV0")
public class ServiceController {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Autowired
    private ProfileServiceImpl profileService;

    // Debugging APIs for setting user sessions
    @Auth(authMode = AuthModeEnum.REGULAR)
    @RequestMapping(Api.DEBUG+"/session")
    public String getUserName(){
        User user = SessionContext.getUser();
        String id = SessionContext.getUserSessionId();
        return user.getFirstName()+", "+user.getLastName()+" Id:"+id.toString();
    }

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

    @Auth(authMode = AuthModeEnum.REGULAR)
    @RequestMapping(Api.PROFILE)
    public String queryProfile(){
        Profile profile = profileService.queryCurrentUserProfile();
        Response<Profile> response = Response.buildSuccessResponse(Profile.class, profile);
        Gson gson = new Gson();
        return gson.toJson(response, Response.class);
    }
}
