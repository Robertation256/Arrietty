package com.arrietty.utils.resttemplates;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DefaultRestTemplate extends RestTemplate {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 2000;

    public DefaultRestTemplate(){
        super();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        this.setRequestFactory(requestFactory);
    }
}
