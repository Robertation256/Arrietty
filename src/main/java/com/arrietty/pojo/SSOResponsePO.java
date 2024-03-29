package com.arrietty.pojo;

import lombok.Data;

@Data
public class SSOResponsePO<T> {
    private Boolean success;
    private String message;
    private Integer code;
    private Long timestamp;
    private T result;

}
