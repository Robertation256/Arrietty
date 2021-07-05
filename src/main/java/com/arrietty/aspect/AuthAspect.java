package com.arrietty.aspect;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;
import com.arrietty.entity.User;
import com.arrietty.service.redis.RedisServiceImpl;
import com.arrietty.utils.response.Response;
import com.arrietty.utils.session.SessionContext;
import com.arrietty.utils.wrappers.HttpServletRequestWrapper;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;



/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 14:19
 */

@Aspect
@Component
@Order(0)
public class AuthAspect {

    @Autowired
    RedisServiceImpl redisService;

    @Around("@annotation(com.arrietty.annotations.Auth)")
    public String authenticateRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        AuthModeEnum authMode = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Auth.class).authMode();
        System.out.println("hello");
        if (authMode.equals(AuthModeEnum.REGULAR)){
            return handleRegularAuth(joinPoint);
        }
        else if (authMode.equals(AuthModeEnum.ADMIN)){
            return handleAdminAuth(joinPoint);
        }

            return (String) joinPoint.proceed();
        }

    private String handleRegularAuth(ProceedingJoinPoint joinPoint) throws Throwable{
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // A demo session auth process by checking "userSessionKey" in user cookie
        //To-do: implement real authentication by checking cookie

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        String userSessionId = requestWrapper.getCookieValue("userSessionId");

        if (userSessionId != null){
            User userInfo = redisService.getUserSession(userSessionId);
            if (userInfo != null){
                SessionContext.initialize(userSessionId,userInfo);
                return (String) joinPoint.proceed();
            }
        }

        Response response =  Response.buildFailedResponse(ErrorCode.UNAUTHORIZED_USER_REQUEST, "Please login first");
        Gson gson = new Gson();
        return gson.toJson(response, Response.class);
    }

    private String handleAdminAuth(ProceedingJoinPoint joinPoint){
        //To-do: add admin authentication
        Response response =  Response.buildFailedResponse(ErrorCode.UNAUTHORIZED_USER_REQUEST, "No admin authority");
        Gson gson = new Gson();
        return gson.toJson(response, Response.class);
    }


}