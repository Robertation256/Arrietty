package com.arrietty.utils.session;

import com.arrietty.consts.ErrorCode;
import com.arrietty.entity.User;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.SessionPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 14:04
 */

public class SessionContext {
    private static final Logger logger = LoggerFactory.getLogger(SessionContext.class);

    private static ThreadLocal<SessionContext> threadLocal = new ThreadLocal<>();
    private String userSessionId;
    private SessionPO userInfo;

    private SessionContext(String userSessionId, SessionPO userInfo){
        this.userSessionId = userSessionId;
        this.userInfo = userInfo;
    }


    public static void initialize(String userSessionId, SessionPO sessionPO){
        SessionContext context = new SessionContext(userSessionId, sessionPO);
        threadLocal.set(context);
    }

    public static String getUserSessionId(){
        SessionContext context = threadLocal.get();
        return context.userSessionId;
    }

    public static Long getUserId(){
        SessionContext context = threadLocal.get();
        if(context.userInfo==null){
            logger.error("user info is null.");
            throw new LogicException(ErrorCode.INTERNAL_ERROR, "Internal error.");
        }
        return context.userInfo.getId();
    }

    public static String getUserNetId(){
        SessionContext context = threadLocal.get();
        return context.userInfo==null? null : context.userInfo.getNetId();
    }

    public static boolean getIsAdmin(){
        SessionContext context = threadLocal.get();
        return context.userInfo.isAdmin();
    }

}
