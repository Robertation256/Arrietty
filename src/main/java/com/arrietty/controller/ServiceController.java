package com.arrietty.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arrietty.consts.Api;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping("/service")
public class ServiceController {

        // profile APIs
        @RequestMapping(Api.PROFILE)
        public String getProfile(String netId){
            return "Hello World";
        }


}
