package com.arrietty.aspect;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.RedirectPolicyEnum;
import com.arrietty.pojo.SessionPO;
import com.arrietty.service.AuthServiceImpl;
import com.arrietty.service.RedisServiceImpl;

import com.arrietty.utils.session.SessionContext;
import com.arrietty.utils.wrappers.HttpServletRequestWrapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 14:19
 */

@Aspect
@Component
@Order(0)
public class AuthAspect {

    public static final Logger logger = LoggerFactory.getLogger(AuthAspect.class);

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private AuthServiceImpl authService;

    @Around("@annotation(com.arrietty.annotations.Auth)")
    public Object authenticateRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Auth annotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Auth.class);
        AuthModeEnum authMode = annotation.authMode();
        RedirectPolicyEnum redirectPolicy = annotation.redirectPolicy();


        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn("RequestAttributes is null.");
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        String userSessionId = requestWrapper.getCookieValue("userSessionId");

        SessionPO session = null;


        // user session expires or user has not yet logged in
        if(userSessionId==null || (session = redisService.getUserSession(userSessionId))==null){
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(httpServletResponse==null){
                logger.warn("HttpServletResponse is null.");
                return null;
            }
            httpServletResponse.setStatus(302);

            //redirect to Shibboleth if redirect policy is turned on
            if(RedirectPolicyEnum.REDIRECT.equals(redirectPolicy)){
                String redirectUrl = authService.getSSOUrl();
                if(redirectUrl==null){
                    // failed to obtain a sso redirect url from keycloak, redirect to 500
                    httpServletResponse.setHeader("Location", "/500");
                }
                else {
                    httpServletResponse.setHeader("Location", redirectUrl);
                }
                return null;
            }

            //redirect to 401 if non-login user visits an API annotated by NO_REDIRECT
            else {
                httpServletResponse.setHeader("Location", "/401");
            }
            return null;
        }

        // check if user is blacklisted
        if(redisService.isNetIdBlacklisted(session.getNetId())){
            redisService.removeUserSession(session.getId());
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(httpServletResponse==null){
                logger.warn("HttpServletResponse is null.");
                return null;
            }
            httpServletResponse.setStatus(302);
            httpServletResponse.setHeader("Location", "/401");
            logger.info(String.format("[netId: %s] user request blocked due to blacklist.", session.getNetId()));
            return null;
        }

        // admin api access control
        if (authMode.equals(AuthModeEnum.ADMIN) && !session.isAdmin()){
            logger.info(String.format("[netId: %s] Unauthorized request on admin API.", session.getNetId()));
            return null;
        }

        // otherwise initialize thread local with user session
        SessionContext.initialize(userSessionId,session);
        // extend user session and cache expiration timeout
        redisService.extendUserSession(userSessionId);
        redisService.extendUserCache(session.getId());

        return  joinPoint.proceed();
    }

}
