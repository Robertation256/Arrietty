package com.arrietty.entity;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/25 17:32
 */


public class Profile {
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    private Long profileId;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getClassYear() {
        return classYear;
    }

    public void setClassYear(Integer classYear) {
        this.classYear = classYear;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getAvatarImageIds() {
        return avatarImageId;
    }

    public void setAvatarImageIds(String avatarImageIds) {
        this.avatarImageId = avatarImageIds;
    }

    private String major;
    private Integer classYear;
    private String schoolYear;
    private String avatarImageId;
}
