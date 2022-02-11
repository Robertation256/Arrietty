package com.arrietty.controller;



import com.arrietty.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.arrietty.consts.Api;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;


    // 接受shibboleth 回调
    @GetMapping(value = "/login")
    public RedirectView login(@RequestParam("token") String token, @RequestParam("clientId") String clientId){
        if(authService.login(token,clientId)){
            return new RedirectView("/home");
        }

        return new RedirectView("/error");
    }
}
