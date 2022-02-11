package com.arrietty.service;


import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.utils.session.SessionIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

@Service
public class AuthServiceImpl {

    @Value("${auth.token-obtain-url}")
    private String TOKEN_OBTAIN_URL;

    @Value("${auth.access-token-obtain-url}")
    private String ACCESS_TOKEN_OBTAIN_URL;

    @Value("${auth.user-info-obtain-url}")
    private String USER_INFO_OBTAIN_URL;

    @Value("${auth.client-id}")
    private static String CLIENT_ID;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisServiceImpl redisService;

    public RedirectView getSSOUrl(){

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
        return true;

    }

    private String getNetIdByToken(String token){
        return "";
    }
}
