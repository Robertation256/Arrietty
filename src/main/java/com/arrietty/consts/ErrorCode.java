package com.arrietty.consts;

public class ErrorCode {
    public static final int INVALID_REQUEST_BODY = 1000;    //request body is illegally formed
    public static final int UNAUTHORIZED_USER_REQUEST = 10001; //Service denial for normal user accessing admin utilities


    //file storage error
    public static final int MAX_IMAGE_SIZE_EXCEEDED = 2000;      // max image size exceeded
    public static final int IMAGE_SAVE_ERROR = 2001;    // general save error
    public static final int BAD_IMAGE_FORMAT = 2002;    // file is not an image
    public static final int IMAGE_NOT_FOUND = 2003;    // image not found
    public static final int IMAGE_LOAD_ERROR = 2004;    // general image load error


    //profile edit error
    public static final int PROFILE_EDIT_ERROR = 3001;


}
