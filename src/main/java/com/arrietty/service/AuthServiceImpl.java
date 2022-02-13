package com.arrietty.service;


import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.pojo.AccessTokenResponsePO;
import com.arrietty.pojo.SSOResponsePO;
import com.arrietty.pojo.TokenResponsePO;
import com.arrietty.pojo.UserInfoResponsePO;
import com.arrietty.utils.session.SessionIdGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl {

    @Value("${auth.token-obtain-url}")
    private String TOKEN_OBTAIN_URL;

    @Value("${auth.access-token-obtain-url}")
    private String ACCESS_TOKEN_OBTAIN_URL;

    @Value("${auth.user-info-obtain-url}")
    private String USER_INFO_OBTAIN_URL;

    @Value("${auth.client-id}")
    private String CLIENT_ID;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RestTemplate restTemplate;


    // 获取SSO url, redirect user 去 SSO 页面
    public String getSSOUrl(){

        String rawResponse = restTemplate.getForObject(
                String.format(TOKEN_OBTAIN_URL,CLIENT_ID,"shibboleth-redirect"),
                String.class);

        Type type = new TypeToken<SSOResponsePO<TokenResponsePO>>(){}.getType();
        SSOResponsePO<TokenResponsePO> response = new Gson().fromJson(rawResponse, type);
        return response.getResult().getUrl();
    }

    public Boolean login(String token, String clientId){
        if(clientId==null || !clientId.equals(CLIENT_ID)){
            return false;
        }

        String netId = getNetIdByToken(token);
        if(netId==null){
            return false;
        }

        User user = userMapper.selectByNetId(netId);

        //create new user account if it doesn't exit
        if(user==null){
            user = new User();
            user.setNetId(netId);
            userMapper.insert(user);
        }

        // insert user session
        String sessionId = SessionIdGenerator.generate();
        redisService.setUserSession(sessionId, user);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        Cookie sessionCookie = new Cookie("userSessionId", sessionId);
        response.addCookie(sessionCookie);

        return true;

    }

    private String getNetIdByToken(String token){
        //TODO: exception handling

        String rawAccessTokenResponse = restTemplate.getForObject(
                String.format(ACCESS_TOKEN_OBTAIN_URL,token),
                String.class);

        Type type1 = new TypeToken<SSOResponsePO<AccessTokenResponsePO>>(){}.getType();
        SSOResponsePO<AccessTokenResponsePO> accessTokenResponse = new Gson().fromJson(rawAccessTokenResponse, type1);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", String.format("Bearer %s", accessTokenResponse.getResult().getAccessToken()));
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, httpHeaders);
        Map<String,String> param = new HashMap<>();
        ResponseEntity<String> rss = restTemplate.exchange(USER_INFO_OBTAIN_URL, HttpMethod.GET, requestEntity, String.class, param);


        Type type2 = new TypeToken<SSOResponsePO<UserInfoResponsePO>>(){}.getType();
        SSOResponsePO<UserInfoResponsePO> userInfoResponse = new Gson().fromJson(rss.getBody(), type2);

        return userInfoResponse.getResult().getUsername();

    }
}
