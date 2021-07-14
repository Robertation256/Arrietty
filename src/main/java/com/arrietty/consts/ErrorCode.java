package com.arrietty.consts;

public class ErrorCode {
    public static final int INVALID_REQUEST_BODY = 1000;    //request body is illegally formed
    public static final int UNAUTHORIZED_USER_REQUEST = 10001; //Service denial for non logged in users


    //file storage error
    public static final int IMAGE_UPLOAD_ERROR = 2000;
    public static final int IMAGE_LOAD_ERROR = 2001;

    //profile edit error
    public static final int PROFILE_EDIT_ERROR = 3001;


}
