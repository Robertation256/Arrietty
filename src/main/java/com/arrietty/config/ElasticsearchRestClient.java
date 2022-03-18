package com.arrietty.config;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchRestClient {


    @Value("${elasticsearch.ip}")
    private String IP;


    @Bean(name="esClient")
    public RestHighLevelClient esClient(){
        System.out.println(IP);
        String[] address = IP.split(":");
        String ip = address[0];
        int port = Integer.parseInt(address[1]);

        HttpHost httpHost =  new HttpHost(ip, port, "http");
        RestClientBuilder clientBuilder = RestClient.builder(httpHost);
        return new RestHighLevelClient(clientBuilder);

//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "88sf8Aii4n1iU4DegTrT"));
//        RestClientBuilder clientBuilder = RestClient.builder(httpHost)
//                .setMaxRetryTimeoutMillis(3500)
//                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                    @Override
//                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                    }
//                });
//        return new RestHighLevelClient(clientBuilder);
    }


}
