package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.UserMapper;
import com.arrietty.entity.User;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfilePO;
import com.arrietty.utils.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@Service
public class ProfileServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final String PROFILE_READ_WRITE_LOCK = "profile_read_write_lock";

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private UserMapper userMapper;

    public ProfilePO getUserProfile(Long userId){
        ProfilePO profile;
        //userId 为空默认获取当前用户profile
        boolean getCurrentUserProfile = false;
        if (userId==null){
            userId = SessionContext.getUserId();
            getCurrentUserProfile = true;
        }

        if ((profile=redisService.getUserProfile(userId))==null){
            // cache miss, go fetch from db
            synchronized (PROFILE_READ_WRITE_LOCK) {
                User user = userMapper.selectByPrimaryKey(userId);
                if (user == null){
                    logger.warn("user profile info not found");
                    throw new LogicException(ErrorCode.INVALID_URL_PARAM, "User does not exist.");
                }
                profile = new ProfilePO();
                profile.setNetId(user.getNetId());
                profile.setUsername(user.getUsername());
                profile.setSchoolYear(user.getSchoolYear());
                profile.setAvatarImageId(user.getAvatarImageId());
                redisService.setUserProfile(userId, profile);
            }
        }

        if(getCurrentUserProfile){
            profile.setIsAdmin(SessionContext.getIsAdmin());
        }
        return profile;
    }


    public void updateUserProfile(ProfilePO profile) throws LogicException {
        checkProfileFormat(profile);
        ProfilePO target = new ProfilePO();

        // only allow update to current user's own profile
        target.setId(SessionContext.getUserId());
        target.setUsername(profile.getUsername());
        target.setSchoolYear(profile.getSchoolYear());

        synchronized (PROFILE_READ_WRITE_LOCK){
            if(!userMapper.updateProfile(target)){
                logger.error("[profile update failed] failed to update db");
                throw new LogicException(ErrorCode.INTERNAL_ERROR, "Internal error.");
            }
            redisService.setUserProfile(target.getId(), target);
        }
    }

    private void checkProfileFormat(ProfilePO profilePO) throws LogicException{
        Integer currentYear = CALENDAR.get(Calendar.YEAR);
        if(
                profilePO==null ||
                        profilePO.getUsername()==null ||
                        profilePO.getUsername().length()>50 ||
                        (profilePO.getSchoolYear()!=null &&
                                (profilePO.getSchoolYear()<2012 || profilePO.getSchoolYear()>currentYear)
                        )
        ){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Invalid profile fields");
        }
    }
}
