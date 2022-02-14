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

    // only update fields that are not null
    public ProfilePO updateUserProfile(ProfilePO profile){
        if(profile==null) return null;

        ProfilePO target = new ProfilePO();
        target.setId(SessionContext.getUserId());
        target.setUsername(profile.getUsername());
        target.setMajor(profile.getMajor());
        target.setSchoolYear(profile.getSchoolYear());
        target.setBio(profile.getBio());
        target.setAvatarImageId(profile.getAvatarImageId());

        if(!userMapper.updateProfile(target)){
            return null;
        }

        // update cache
        ProfilePO oldCache = redisService.getUserProfile(SessionContext.getUserId());
        if (oldCache!=null){
            oldCache.setUsername(profile.getUsername()==null? oldCache.getUsername():profile.getUsername());
            oldCache.setMajor(profile.getMajor()==null? oldCache.getMajor():profile.getMajor());
            oldCache.setSchoolYear(profile.getSchoolYear()==null?oldCache.getSchoolYear():profile.getSchoolYear());
            oldCache.setBio(profile.getBio()==null?oldCache.getBio():profile.getBio());
            oldCache.setAvatarImageId(profile.getAvatarImageId()==null?oldCache.getAvatarImageId():profile.getAvatarImageId());
            redisService.setUserProfile(target.getId(), oldCache);
        }

        return getUserProfile(SessionContext.getUserId());
    }


}
