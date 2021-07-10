package com.arrietty.pojo;

import lombok.Data;

@Data
public class GetActiveTokenResponseResultPO {
    private String access_token;
    private Integer expires_in;
    private Integer refresh_expires_in;
    private String refresh_token;
    private String token_type;
    private String id_token;
    private Integer not_before_policy;
    private String session_state;
    private String scope;

}
