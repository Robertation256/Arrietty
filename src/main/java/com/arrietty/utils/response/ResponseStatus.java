package com.arrietty.utils.response;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/2 16:59
 */
public class ResponseStatus {
    private String status;
    private String message;
    public void setStatus(String status){this.status = status;}
    public void setMessage(String message){this.message = message;}
    public String getStatus(){return this.status;}
    public String getMessage(){return this.message;}
}
