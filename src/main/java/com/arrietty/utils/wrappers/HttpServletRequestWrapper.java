package com.arrietty.utils.wrappers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 16:54
 */
public class HttpServletRequestWrapper {

    private Map<String, String> cookieMap;
    private HttpServletRequest rawServletRequest;

    public HttpServletRequestWrapper(HttpServletRequest request){
        this.rawServletRequest = request;
        Map<String, String> cookieMap = new HashMap<>(request.getCookies().length);
        for (Cookie c: request.getCookies()){
            cookieMap.put(c.getName(),c.getValue());
        }
        this.cookieMap = cookieMap;
    }

    public String getCookieValue(String cookieName){
        return this.cookieMap.getOrDefault(cookieName,null);
    }

}
