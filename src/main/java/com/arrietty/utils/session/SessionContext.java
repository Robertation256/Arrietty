package com.arrietty.utils.session;

import com.arrietty.entity.User;
import com.arrietty.pojo.SessionPO;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 14:04
 */

public class SessionContext {
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
        return context.userInfo==null? null : context.userInfo.getId();
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
