package com.arrietty.consts;

public class RedisKey {

    // access control
    public static final String BLACKLISTED_USER_NET_ID_SET = "blacklisted_user_net_id_set";
    public static final String USER_SESSION = "user_session:user_id=";


    public static final String USER_PROFILE = "user_profile:user_Id=";


    public static final String ALL_TEXTBOOK_TAG_ID = "all_textbook_tag_id";

    public static final String CURRENT_USER_TAPPED_AD_ID_LIST = "tapped_ad_id_list:user_id=";

    //remove on ad delete
    public static final String NUMBER_OF_TAPS = "number_of_taps:ad_id=";

    public static final String CURRENT_USER_MARKED_AD_ID_LIST = "marked_ad_id_list:user_id=";

    // last modified timestamp
    public static final String AD_TIMESTAMP = "ad_timestamp";

    // user has unread notification
    public static final String USER_NOTIFICATION_HAS_NEW = "user_notification_has_new:user_id=";

    // stores the json string of all bulletins
    public static final String BULLETIN_CACHE = "bulletin_cache";


    // admin statistics
    public static final String USER_AD_UPLOAD_NUM = "user_ad_upload_num";
    public static final String USER_AD_UPDATE_NUM = "user_ad_update_num";
    public static final String USER_AD_DELETE_NUM = "user_ad_delete_num";

    public static final String USER_MARK_NUM = "user_mark_num";
    public static final String USER_UNMARK_NUM = "user_unmark_num";

    public static final String USER_SEARCH_NUM = "user_search_num";
}
