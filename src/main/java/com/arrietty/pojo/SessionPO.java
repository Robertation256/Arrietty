package com.arrietty.pojo;


import lombok.Data;

import java.util.Date;

@Data
public class SessionPO {
    private Long id;
    private String netId;
    private Boolean isAdmin;
    private Date lastLoginTime;
}
