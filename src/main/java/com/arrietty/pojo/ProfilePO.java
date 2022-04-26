package com.arrietty.pojo;


import lombok.Data;

@Data
public class ProfilePO {
    private Long id;
    private String username;
    private String netId;
    private String major;
    private String schoolYear;
    private Long avatarImageId;
    private String bio;
    private Boolean isAdmin;
}
