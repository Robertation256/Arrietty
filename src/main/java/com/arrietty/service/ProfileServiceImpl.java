package com.arrietty.service;


import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl {

    @Autowired
    ProfileMapper profileMapper;


    public Profile queryCurrentUserProfile(){
        //obtain userId from session
        Long userId = SessionContext.getUserId();
        Profile profile = profileMapper.queryByUserId(userId);
        return  profile;
    }


}
