package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.ImageMapper;
import com.arrietty.entity.Image;
import com.arrietty.entity.Profile;
import com.arrietty.dao.ProfileMapper;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.ProfileResponseType;
import com.arrietty.utils.response.Response;
import com.arrietty.utils.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl {

    @Autowired
    ProfileMapper profileMapper;

    @Autowired
    ImageMapper imageMapper;


    public Response<ProfileResponseType> queryCurrentUserProfile(){
        //obtain userId from session
        Long userId = SessionContext.getUserId();
        Profile profile = profileMapper.queryByUserId(userId);

        ProfileResponseType profileResponseType = new ProfileResponseType();
        profileResponseType.setNetId(SessionContext.getUser().getNetId());
        profileResponseType.setSchoolYear(profile.getSchoolYear());
        profileResponseType.setClassYear(profile.getClassYear());
        profileResponseType.setMajor(profile.getMajor());
        profileResponseType.setBio(profile.getBio());

        if (profile.getAvatarImageId() != null){
            Image image = imageMapper.selectByPrimaryKey(profile.getAvatarImageId());
            String imageId = image==null?"":image.getExternalImageId();
            profileResponseType.setAvatarImageId(imageId);
        }

        return  Response.buildSuccessResponse(ProfileResponseType.class, profileResponseType);
    }

    public Response<String> updateUserProfile(Profile profile){
        Long userId = SessionContext.getUserId();

        //Get original profile
        Profile originalProfile = profileMapper.queryByUserId(userId);
        if (originalProfile == null){
            throw new LogicException(ErrorCode.PROFILE_EDIT_ERROR, "Profile does not exist.");
        }
        profile.setProfileId(originalProfile.getProfileId());
        profile.setAvatarImageId(originalProfile.getAvatarImageId());
        profile.setUserId(originalProfile.getUserId());
        profileMapper.updateByPrimaryKey(profile);
        return Response.buildSuccessResponse();
    }


}
