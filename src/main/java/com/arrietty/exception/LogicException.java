package com.arrietty.exception;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/9 18:17
 */
public class LogicException extends RuntimeException{
    public int errorCode;
    public String errorMessage;
    public LogicException(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
