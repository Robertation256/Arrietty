package com.arrietty.pojo;


import lombok.Data;

@Data
public class ProfilePO {
    private Long id;
    private String username;
    private String netId;
    private Integer schoolYear;
    private Long avatarImageId;
    private Boolean isAdmin;
}
