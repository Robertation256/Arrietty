package com.arrietty.pojo;

import lombok.Data;

@Data
public class GetActiveTokenResponsePo {
    private Boolean success;
    private String message;
    private Integer code;
    private Long timestamp;
    private GetActiveTokenResponseResultPO result;
}
