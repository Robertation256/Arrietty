package com.arrietty.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String netId;
    private String firstName;
    private String lastName;
    private Date lastLoginTime;
    private String accountStatus;

}
