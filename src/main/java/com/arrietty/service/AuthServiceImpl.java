package com.arrietty.service;

import com.arrietty.entity.User;
import com.arrietty.pojo.GetActiveTokenResponsePo;
import com.arrietty.pojo.GetSSOUrlResponsePO;
import com.arrietty.pojo.GetUserInfoResponsePO;
import com.arrietty.utils.resttemplates.DefaultRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;


@Service
public class AuthServiceImpl {

    private static final String GET_SSO_URL = "http://review.shanghai.nyu.edu/app/rest/keycloak/auth?clientId={clientId}&authType={authType}";
    private static final String GET_ACCESS_TOKEN_URL = "https://review.shanghai.nyu.edu/app/rest/keycloak/token?token={token}";
    private static final String GET_USER_INFO_URL = "https://review.shanghai.nyu.edu/app/rest/keycloak/welcome";

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${auth.client-id}")
    private String clientId;

    @Autowired
    DefaultRestTemplate defaultRestTemplate;

    public String getSSOUrl(){
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("clientId","av-center");
        paramMap.put("authType","shibboleth-redirect");
        try {
            GetSSOUrlResponsePO response = defaultRestTemplate.getForObject(GET_SSO_URL,GetSSOUrlResponsePO.class,paramMap);
            if (response == null || response.getResult() == null){
                logger.warn("[method=getSSOUrl] obtain SSO URL failed.");
                return null;
            }
            return response.getResult().getUrl();
        }
        catch (Exception e ){
            logger.warn("[method=getSSOUrl] obtain SSO URL failed.");
        }
        return null;
    }

    public User getUserInfoByToken(String token){

        String accessToken = null;
        Map<String, String> paramMap = new HashMap<>(1);
        paramMap.put("token",token);
        try {
            GetActiveTokenResponsePo response = defaultRestTemplate.getForObject(GET_ACCESS_TOKEN_URL,GetActiveTokenResponsePo.class,paramMap);
            if (response == null || ! response.getSuccess() || response.getResult() == null){
                logger.warn("[method=getUserInfoByToken] obtain access token failed.");
                return null;
            }
            accessToken = response.getResult().getAccess_token();
        }
        catch (Exception e ){
            logger.warn("[method=getSSOUrl] obtain access token failed.");
        }

        if (accessToken == null) return null;


        // get user info by access token
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken );
        HttpEntity<String> entity = new HttpEntity<>("body", httpHeaders);
        try {
            ResponseEntity<GetUserInfoResponsePO> resObj = defaultRestTemplate.exchange(GET_USER_INFO_URL, HttpMethod.GET,entity,GetUserInfoResponsePO.class);
            GetUserInfoResponsePO response = resObj.getBody();
            if (response == null || ! response.getSuccess() || response.getResult() == null){
                logger.warn("[method=getUserInfoByToken] obtain user info failed.");
                return null;
            }
            User user = new User();
            user.setNetId(response.getResult().getUsername());
            user.setFirstName(response.getResult().getFirstName());
            user.setLastName(response.getResult().getLastName());
            user.setLastLoginTime(new Date());
            return user;
        }
        catch (Exception e ){
            logger.warn("[method=getSSOUrl] obtain user info failed.");
        }
        return null;
    }

}
