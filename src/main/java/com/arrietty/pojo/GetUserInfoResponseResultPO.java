package com.arrietty.pojo;

import lombok.Data;

@Data
public class GetUserInfoResponseResultPO {
    private Long createdTimeStamp;
    private String username;
    private String firstName;
    private String lastName;
}
