package com.arrietty.aspect;

import com.arrietty.consts.ErrorCode;
import com.arrietty.exception.LogicException;
import com.arrietty.utils.response.Response;
import com.arrietty.utils.session.SessionContext;
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



    @Around("@annotation(com.arrietty.annotations.Log)")
    public Object controllerLog(ProceedingJoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn("RequestAttributes is null.");
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        Object response;
        try{
            response = joinPoint.proceed();
            if(response instanceof String){
                formattedLog(request, joinPoint, (String)response);
                return response;
            }
            return response;
        }
        catch (LogicException e){
            String json = new Gson().toJson(Response.buildFailedResponse(e.errorCode, e.errorMessage));
            formattedLog(request, joinPoint, json);
            return json;
        }
        // runtime error
        catch (Throwable e){
            logger.warn("[Warning] "+e.toString());
        }

        return new Gson().toJson(Response.buildFailedResponse(ErrorCode.INTERNAL_ERROR, "Internal error."));
    }

    private void formattedLog(HttpServletRequest request,ProceedingJoinPoint joinPoint,  String response){
        String s = String.format("[netId: %s][method: %s] [url: %s] [url_param: %s] [body: %s] [response: %s]",
                SessionContext.getUserNetId(),
                request.getMethod(),
                request.getRequestURL(),
                request.getQueryString(),
                new Gson().toJson(joinPoint.getArgs()),
                response
        );

        logger.info(s);
    }

}
