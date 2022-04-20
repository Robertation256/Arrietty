package com.arrietty.aspect;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;
import com.arrietty.exception.LogicException;
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
        AuthModeEnum authMode = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Auth.class).authMode();

        // regular user auth
        if (authMode.equals(AuthModeEnum.REGULAR)){
            return handleRegularAuth(joinPoint);
        }
        // admin auth
        else if (authMode.equals(AuthModeEnum.ADMIN)){
            return handleAdminAuth(joinPoint);
        }

            return  joinPoint.proceed();
        }

    private Object handleRegularAuth(ProceedingJoinPoint joinPoint) throws Throwable{
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn("RequestAttributes is null.");
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        String userSessionId = requestWrapper.getCookieValue("userSessionId");

        SessionPO session = null;

        // user session expires or user has not yet logged in, redirect to Shibboleth
        if(userSessionId==null || (session = redisService.getUserSession(userSessionId))==null){
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(httpServletResponse==null){
                logger.warn("HttpServletResponse is null.");
                return null;
            }
            httpServletResponse.setHeader("Location", authService.getSSOUrl());
            httpServletResponse.setStatus(302);
            logger.info("Redirect to Shibboleth SSO");
            return null;
        }

        // otherwise initialize thread local with user session
        SessionContext.initialize(userSessionId,session);

        // extend user session and cache expiration timeout
        redisService.extendUserSession(userSessionId);
        redisService.extendUserCache(session.getId());

        return joinPoint.proceed();

    }

    private Object handleAdminAuth(ProceedingJoinPoint joinPoint) throws Throwable{
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn("RequestAttributes is null.");
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        String userSessionId = requestWrapper.getCookieValue("userSessionId");

        SessionPO session = null;

        // user session expires or user has not yet logged in, redirect to Shibboleth
        if(userSessionId==null || (session = redisService.getUserSession(userSessionId))==null){
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(httpServletResponse==null){
                logger.warn("HttpServletResponse is null.");
                return null;
            }
            httpServletResponse.setHeader("Location", authService.getSSOUrl());
            httpServletResponse.setStatus(302);
            return null;

        }

        if(!session.isAdmin()){
            logger.info(String.format("[netId: %s] Unauthorized request on admin API.", session.getNetId()));
            return null;
        }

        // otherwise initialize thread local with user session
        SessionContext.initialize(userSessionId,session);

        // extend user session and cache expiration timeout
        redisService.extendUserSession(userSessionId);
        redisService.extendUserCache(session.getId());

        return joinPoint.proceed();

    }


}
