package com.arrietty.pojo;


import lombok.Data;

import java.util.Date;

@Data
public class TapPO {
    private Long id;
    private String username;
    private String netId;
    private Long avatarImageId;
    private String adTitle;
    private Date createTime;
}
