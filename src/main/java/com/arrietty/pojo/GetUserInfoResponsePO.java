package com.arrietty.pojo;

import lombok.Data;

@Data
public class GetUserInfoResponsePO {
    private Boolean success;
    private String message;
    private Integer code;
    private Long timestamp;
    private GetUserInfoResponseResultPO result;
}
