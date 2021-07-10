package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;
import com.arrietty.entity.Profile;
import com.arrietty.entity.User;
import com.arrietty.service.AuthServiceImpl;
import com.arrietty.service.ProfileServiceImpl;
import com.arrietty.service.redis.RedisServiceImpl;
import com.arrietty.utils.response.Response;
import com.arrietty.utils.session.SessionContext;
import com.arrietty.utils.session.SessionIdGenerator;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.arrietty.consts.Api;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;



/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping(Api.SERVICE_VERSION)
public class ServiceController {

    @Value("${auth.client-id}")
    private String clientId;

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private AuthServiceImpl authService;

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





    /**
     *
     * Interfaces for testing Shibboleth
     * */
    @RequestMapping(Api.AUTH+"/login")
    public ModelAndView handleLoginSSORedirect(){
        String ssoUrl = authService.getSSOUrl();
        return new ModelAndView("redirect:"+ssoUrl);
    }

    @RequestMapping(Api.AUTH+"/redirect")
    public Object handleShibbolethCallBack(@RequestParam("token") String token, @RequestParam("clientId") String clientId){
        if (!this.clientId.equals(clientId)){
            return Response.buildFailedResponse(ErrorCode.UNAUTHORIZED_USER_REQUEST, "No client ID");
        }

        if (token == null){
            return Response.buildFailedResponse(ErrorCode.UNAUTHORIZED_USER_REQUEST, "Token is empty");
        }

        return authService.getUserInfoByToken(token);
    }


}
