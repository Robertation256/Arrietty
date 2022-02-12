package com.arrietty.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AccessTokenResponsePO {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("refresh_expires_in")
    private Integer refreshExpiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("id_token")
    private String idToken;

    @SerializedName("not-before-policy")
    private Integer notBeforePolicy;

    @SerializedName("session_state")
    private String sessionState;

    private String scope;
}
