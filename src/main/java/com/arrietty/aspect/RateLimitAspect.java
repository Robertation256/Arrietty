package com.arrietty.aspect;


import com.arrietty.annotations.Auth;
import com.arrietty.annotations.RateLimit;
import com.arrietty.service.RedisServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

@Aspect
@Component
@Order(0)
public class RateLimitAspect {

    public static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    private RedisServiceImpl redisService;

    @Around("@annotation(com.arrietty.annotations.RateLimit)")
    public Object rateLimitHandler(ProceedingJoinPoint joinPoint) throws Throwable{
        RateLimit annotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(RateLimit.class);
        int RATE_LIMIT = annotation.rateLimit();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn("RequestAttributes is null.");
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String ip = request.getRemoteAddr();

        long requestNum = redisService.incrementRequestNum(ip);
        if(requestNum > RATE_LIMIT){
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(httpServletResponse==null){
                logger.warn("HttpServletResponse is null.");
                return null;
            }
            httpServletResponse.setStatus(401);
            // log upon exceeding the threshold
            if(requestNum==RATE_LIMIT+1){
                logger.warn(String.format("[Rate limit exceeded] [ip: %s] [url: %s]", ip, request.getRequestURL()));
            }
            return null;
        }

        return joinPoint.proceed();
    }
}
