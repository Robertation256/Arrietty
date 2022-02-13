package com.arrietty.service;


import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private UserMapper userMapper;

    public ProfilePO getUserProfile(Long userId){
        if (userId==null){
            return null;
        }

        ProfilePO profile;

        // cache miss, go fetch from db
        if ((profile=redisService.getUserProfile(userId))==null){
            User user = userMapper.selectById(userId);
            if(user==null) return null;
            profile = new ProfilePO();
            profile.setNetId(user.getNetId());
            profile.setUsername(user.getUsername());
            profile.setSchoolYear(user.getSchoolYear());
            profile.setMajor(user.getMajor());
            profile.setBio(user.getBio());
            profile.setAvatarImageId(user.getAvatarImageId());
            redisService.setUserProfile(userId, profile);
        }

        return profile;
    }

    public ProfilePO updateUserProfile(ProfilePO profile){
        if(profile==null) return null;

        ProfilePO target = new ProfilePO();
        target.setId(SessionContext.getUserId());
        target.setUsername(profile.getUsername());
        target.setMajor(profile.getMajor());
        target.setSchoolYear(profile.getSchoolYear());
        target.setBio(profile.getBio());

        if(!userMapper.updateProfile(target)){
            return null;
        }

        // update cache
        target.setNetId(SessionContext.getUser().getNetId());
        redisService.setUserProfile(target.getId(), target);
        return target;
    }


}
