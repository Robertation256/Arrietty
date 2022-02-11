package com.arrietty.utils.session;

import com.arrietty.entity.User;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 14:04
 */

public class SessionContext {
    private static ThreadLocal<SessionContext> threadLocal = new ThreadLocal<>();
    private String userSessionId;
    private User userInfo;

    private SessionContext(String userSessionId, User userInfo){
        this.userSessionId = userSessionId;
        this.userInfo = userInfo;
    }


    public static void initialize(String userSessionId, User userInfo){
        SessionContext context = new SessionContext(userSessionId, userInfo);
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

    public static User getUser(){
        SessionContext context = threadLocal.get();
        return context.userInfo;
    }

}
