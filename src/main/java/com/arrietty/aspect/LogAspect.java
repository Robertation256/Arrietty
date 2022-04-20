package com.arrietty.aspect;

import com.arrietty.consts.ErrorCode;
import com.arrietty.exception.LogicException;
import com.arrietty.utils.response.Response;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    public Object controllerLog(ProceedingJoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        Object response;
        try{
            response = joinPoint.proceed();
            if(response instanceof String){
                formattedLog(request,(String)response);
                return response;
            }
            return response;
        }
        catch (LogicException e){
            String json = new Gson().toJson(Response.buildFailedResponse(e.errorCode, e.errorMessage));
            formattedLog(request,json);
            return json;
        }
        // runtime error
        catch (Throwable e){
            logger.error(e.toString());
        }

        return new Gson().toJson(Response.buildFailedResponse(ErrorCode.INTERNAL_ERROR, "Internal error."));
    }

    private void formattedLog(HttpServletRequest request, String response){
        String s = String.format("%s - [%s] [%s] [%s]\n[response] %s",
                dtf.format(LocalDateTime.now()),
                request.getMethod(),
                request.getRequestURL(),
                request.getQueryString(),
                response
        );

        logger.info(s);
    }

}
