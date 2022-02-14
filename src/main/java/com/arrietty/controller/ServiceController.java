package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;

import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.service.ImageServiceImpl;
import com.arrietty.service.ProfileServiceImpl;
import com.arrietty.service.RedisServiceImpl;
import com.arrietty.utils.response.Response;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@Controller
public class ServiceController {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private ImageServiceImpl imageService;



    @Auth(authMode=AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/home")
    public ModelAndView userHome(){
        return new ModelAndView("index.html");
    }


    // 修改用户本人profile
    @Auth(authMode=AuthModeEnum.REGULAR)
    @ResponseBody
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
    @ResponseBody
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

    // handles user avatar image update
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam("file") MultipartFile uploadedFile) throws LogicException {
        imageService.updateAvatar(uploadedFile);
        return new Gson().toJson(Response.buildSuccessResponse());
    }

    //return user avatar image
    @Auth(authMode = AuthModeEnum.REGULAR)
    @GetMapping("/avatar")
    public void getAvatar(HttpServletResponse response) throws LogicException{
        imageService.getAvatar(response);
    }

}
