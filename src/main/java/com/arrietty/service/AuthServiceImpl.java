package com.arrietty.service;


import com.arrietty.consts.AccessControl;
import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.pojo.*;
import com.arrietty.utils.session.SessionIdGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl {

    public static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${auth.token-obtain-url}")
    private String SSO_REDIRECT_URL_OBTAIN_URL;

    @Value("${auth.access-token-obtain-url}")
    private String ACCESS_TOKEN_OBTAIN_URL;

    @Value("${auth.user-info-obtain-url}")
    private String USER_INFO_OBTAIN_URL;

    @Value("${auth.client-id}")
    private String CLIENT_ID;

    @Value("${auth.disable-auto-create-new-user}")
    private String DISABLE_AUTO_CREATE_NEW_USER;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RestTemplate restTemplate;

    private static final Type GET_SSO_REDIRECT_URL_RESPONSE_TYPE = new TypeToken<SSOResponsePO<TokenResponsePO>>(){}.getType();
    private static final Type GET_ACCESS_TOKEN_RESPONSE_TYPE = new TypeToken<SSOResponsePO<AccessTokenResponsePO>>(){}.getType();
    private static final Type USER_INFO_RESPONSE_TYPE = new TypeToken<SSOResponsePO<UserInfoResponsePO>>(){}.getType();

    // 获取SSO url, redirect user 去 SSO 页面
    public String getSSOUrl(){
//        String rawResponse = restTemplate.getForObject(
//                SSO_REDIRECT_URL_OBTAIN_URL,
//                String.class);
//
//        SSOResponsePO<TokenResponsePO> response = new Gson().fromJson(rawResponse, GET_SSO_REDIRECT_URL_RESPONSE_TYPE);
//        if(response==null || response.getResult()==null){
//            logger.error("SSO redirect url request failed");
//            return null;
//        }
//        return response.getResult().getUrl();
        return "http://localhost:8001/sso.html";
    }


    public Boolean login(String token, String clientId){
//        if(clientId==null || !clientId.equals(CLIENT_ID)){
//            return false;
//        }
//
//        String netId = getNetIdByToken(token);
        String netId = clientId;

        if (netId==null){
            return false;
        }

        // this user is blacklisted revoke session injection
        if(redisService.isNetIdBlacklisted(netId)){
            logger.info(String.format("[netId: %s] Login failed due to blacklist.", netId));
            return false;
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            logger.warn(String.format("[netId: %s] RequestAttributes is null.",netId));
            return false;
        }
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        if(response==null){
            logger.warn(String.format("[netId: %s] HttpServletResponse is null.",netId));
            return false;
        }

        User user = userMapper.selectByNetId(netId);
        //create new user account if it doesn't exit
        if(user==null){
            // only allow existing user login, switch on for internal testing purpose
            if("true".equals(DISABLE_AUTO_CREATE_NEW_USER)){
                return false;
            }

            user = new User();
            user.setNetId(netId);
            user.setAccessControl(AccessControl.REGULAR);
            userMapper.insert(user);
            logger.info(String.format("[netId: %s] New user account created.", netId));
        }
        else {
            user.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKey(user);
        }


        // insert user session
        SessionPO sessionPO = new SessionPO();
        sessionPO.setId(user.getId());
        sessionPO.setNetId(user.getNetId());
        sessionPO.setAccessControl(user.getAccessControl());


        String sessionId = SessionIdGenerator.generate();
        redisService.setUserSession(sessionId, sessionPO);
        logger.info(String.format("[netId: %s] User login succeeds.",netId));

        // load user info cache
        redisService.loadUserCache(user);
        logger.debug(String.format("[netId: %s] User cache loaded", netId));

        Cookie sessionCookie = new Cookie("userSessionId", sessionId);
        response.addCookie(sessionCookie);

        return true;
    }

    private String getNetIdByToken(String token){
        String rawAccessTokenResponse = restTemplate.getForObject(
                String.format(ACCESS_TOKEN_OBTAIN_URL,token),
                String.class);

        SSOResponsePO<AccessTokenResponsePO> accessTokenResponse = new Gson().fromJson(rawAccessTokenResponse, GET_ACCESS_TOKEN_RESPONSE_TYPE);
        if(accessTokenResponse==null || accessTokenResponse.getResult()==null){
            logger.error("obtain access token failed");
            return null;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessTokenResponse.getResult().getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, httpHeaders);
        Map<String,String> param = new HashMap<>();
        ResponseEntity<String> rss = restTemplate.exchange(USER_INFO_OBTAIN_URL, HttpMethod.GET, requestEntity, String.class, param);

        SSOResponsePO<UserInfoResponsePO> userInfoResponse = new Gson().fromJson(rss.getBody(), USER_INFO_RESPONSE_TYPE);
        if(userInfoResponse==null || userInfoResponse.getResult()==null || userInfoResponse.getResult().getUsername()==null){
            logger.error("obtain user info failed");
            return null;
        }
        return userInfoResponse.getResult().getUsername();
    }
}
