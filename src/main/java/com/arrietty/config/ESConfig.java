package com.arrietty.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/13 10:03
 */

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ESConfig {
    private String host;
    private int port;

    public void setHost(String host){this.host = host;}
    public void setPort(int port){this.port = port;}

    public String getHost(){return this.host;}
    public int getPort(){return this.port;}

    @Bean
    public RestHighLevelClient esClient(){
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(host, port, "http")
        ));
    }

}
