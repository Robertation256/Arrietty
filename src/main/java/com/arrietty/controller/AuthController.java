package com.arrietty.controller;



import com.arrietty.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.arrietty.consts.Api;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Value("${server.domain}")
    private String DOMAIN;

    @Autowired
    private AuthServiceImpl authService;


    // 接受shibboleth 回调
    @GetMapping(value = "/SSOCallback")
    public String ssoCallback(@RequestParam("token") String token, @RequestParam("clientId") String clientId){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();

        response.setStatus(302);


        if(authService.login(token,clientId)){
            response.setHeader("Location", DOMAIN+"/home");
            return "login success";
        }

        // login failed redirect to 401 page
        response.setHeader("Location", DOMAIN+"/401");
        return "login failed";
    }



}
