package com.arrietty.pojo;

import lombok.Data;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/14 14:04
 */

@Data
public class ProfileResponseType {
    private String netId;
    private String firstName;
    private String lastName;
    private Integer schoolYear;
    private Integer classYear;
    private String major;
    private String bio;
    private String avatarImageId;
}
