package com.arrietty.pojo;

import lombok.Data;

@Data
public class GetSSOUrlResponseResultPO {
    private String clientId;
    private String url;
    private String token;
}
