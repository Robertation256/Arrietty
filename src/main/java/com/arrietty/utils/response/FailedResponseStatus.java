package com.arrietty.utils.response;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/2 17:00
 */
public class FailedResponseStatus extends ResponseStatus {
    private Integer errorCode;
    public void setErrorCode(int errorCode){this.errorCode = errorCode;};
    public Integer getErrorCode(){return this.errorCode;}
}
