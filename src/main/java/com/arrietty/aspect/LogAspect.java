package com.arrietty.aspect;

import com.arrietty.utils.response.Response;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/2 15:18
 */

@Aspect
@Component
@Order(1)
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Pointcut("execution(* com.arrietty.controller.*.*(..))")
    public void controllerLogPointCut(){}

    @Around("controllerLogPointCut()")
    public Object controllerLog(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        Object response = joinPoint.proceed();

        String s1 = String.format("%s - [%6s] [%15s] [%30s] - [%6s] ",
                dtf.format(LocalDateTime.now()),
                request.getMethod(),
                request.getRequestURL(),
                request.getQueryString(),
                response.toString()
                );

        logger.info(s1);
        return response;
    }

}
