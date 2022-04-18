package com.arrietty.pojo;


import com.arrietty.consts.AccessControl;
import lombok.Data;

import java.util.Date;

@Data
public class SessionPO {
    private Long id;
    private String netId;
    private String accessControl;
    private Date lastLoginTime;


    public boolean isAdmin(){
        return AccessControl.ADMIN.equals(accessControl);
    }
}
