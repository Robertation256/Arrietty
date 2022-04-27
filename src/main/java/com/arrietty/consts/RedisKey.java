package com.arrietty.consts;

public class RedisKey {
    /* -----------------------initialized on service start------------------------------------- */
    // these key should never expire unless there is a service restart

    // global user read info
    public static final String VALID_TEXTBOOK_TAG_ID_SET = "all_textbook_tag_id";
    public static final String AD_TIMESTAMP = "ad_timestamp";
    public static final String BULLETIN_CACHE = "bulletin_cache";
    // admin statistics, reset daily
    public static final String USER_AD_UPLOAD_NUM = "user_ad_upload_num";
    public static final String USER_AD_UPDATE_NUM = "user_ad_update_num";
    public static final String USER_AD_DELETE_NUM = "user_ad_delete_num";
    public static final String USER_MARK_NUM = "user_mark_num";
    public static final String USER_UNMARK_NUM = "user_unmark_num";
    public static final String USER_SEARCH_NUM = "user_search_num";

    // access control
    public static final String BLACKLISTED_USER_NET_ID_SET = "blacklisted_user_net_id_set";

    /* -----------------------initialized on service start------------------------------------- */



    /* -----------------------initialized on user login------------------------------------- */
    // extend timeout by 15 minutes upon each API request

    public static final String USER_SESSION = "user_session:user_id=";
    // initialized on user login or other user's read on profile
    public static final String USER_PROFILE = "user_profile:user_id=";

    // user local info
    public static final String CURRENT_USER_TAPPED_AD_ID_LIST = "tapped_ad_id_list:user_id=";
    public static final String CURRENT_USER_MARKED_AD_ID_LIST = "marked_ad_id_list:user_id=";
    public static final String USER_NOTIFICATION_HAS_NEW = "user_notification_has_new:user_id=";

    /* -----------------------initialized on user login------------------------------------- */





    /* -----------------------initialized on ad read------------------------------------- */
    // extend expiration timeout by 1 hour upon each read

    // initialize on ad upload, remove on ad delete
    public static final String NUMBER_OF_TAPS = "number_of_taps:ad_id=";

    /* -----------------------initialized on ad read------------------------------------- */



    /* -----------------------initialized on image read------------------------------------- */
    //delete on image delete
    public static final String IMAGE_OWNER_ID = "image_owner_id:image_id=";





}
