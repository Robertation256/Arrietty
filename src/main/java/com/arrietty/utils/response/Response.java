package com.arrietty.utils.response;

import lombok.Data;

import java.util.List;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/2 15:51
 */
public class Response<T> {

    private ResponseStatus responseStatus;
    private T body;

    public static <M> Response<M> buildSuccessResponse(Class<M> clazz){
        Response<M> response = new Response<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setStatus("Ok");
        responseStatus.setMessage("Success");
        response.setResponseStatus(responseStatus);
        return response;
    }

    public static <M> Response<M> buildSuccessResponse(Class<M> clazz, M body){
        Response<M> response = buildSuccessResponse(clazz);
        response.setBody(body);
        return response;
    }

    public static <M> Response<List<M>> buildSuccessResponse(Class<M> clazz, List<M> body){
        Response<List<M>> response = new Response<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setStatus("Ok");
        responseStatus.setMessage("Success");
        response.setResponseStatus(responseStatus);
        response.setBody(body);
        return response;
    }

    public static Response<String> buildSuccessResponse(){
        return buildSuccessResponse(String.class, "Success");
    }


    public static Response buildFailedResponse(){
        Response response = new Response();
        FailedResponseStatus responseStatus = new FailedResponseStatus();
        responseStatus.setStatus("Error");
        response.setResponseStatus(responseStatus);
        return response;
    }

    public static Response buildFailedResponse(int errorCode, String errorMessage){
        Response response = new Response();
        FailedResponseStatus responseStatus = new FailedResponseStatus();
        responseStatus.setStatus("Error");
        responseStatus.setErrorCode(errorCode);
        responseStatus.setMessage(errorMessage);
        response.setResponseStatus(responseStatus);
        return response;
    }

    public void setResponseStatus(ResponseStatus responseStatus){this.responseStatus = responseStatus;}
    public ResponseStatus getResponseStatus(){return this.responseStatus;}
    public void setBody(T body){this.body = body;}
    public T getBody(){return this.body;}
}



