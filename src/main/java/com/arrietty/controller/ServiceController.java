package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;

import com.arrietty.pojo.ProfilePO;
import com.arrietty.service.ProfileServiceImpl;
import com.arrietty.service.RedisServiceImpl;
import com.arrietty.utils.response.Response;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



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



    @Auth(authMode=AuthModeEnum.REGULAR)
    @GetMapping("/home")
    public String userHome(){
        return "welcome";
    }


    // 修改用户本人profile
    @Auth(authMode=AuthModeEnum.REGULAR)
    @PostMapping("/profile")
    public String postProfile(@RequestBody ProfilePO profilePO){
        Response<ProfilePO> response;
        ProfilePO res = profileService.updateUserProfile(profilePO);
        if(res!=null){
            response = Response.buildSuccessResponse(ProfilePO.class, res);
        }
        else{
            response = Response.buildFailedResponse(ErrorCode.PROFILE_EDIT_ERROR, "update profile failed");
        }

        return new Gson().toJson(response);
    }


    @Auth(authMode=AuthModeEnum.REGULAR)
    @GetMapping("/profile")
    public String getProfile(@RequestParam("userId") Long userId){
        Response<ProfilePO> response;
        ProfilePO profilePO = profileService.getUserProfile(userId);
        if(profilePO!=null){
            response = Response.buildSuccessResponse(ProfilePO.class, profilePO);
        }
        else{
            response = Response.buildFailedResponse();
        }

        return new Gson().toJson(response);
    }

}
