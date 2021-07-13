package com.arrietty.service;

import com.arrietty.entity.Post;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/13 10:30
 */
@Service
public class ESServiceImpl {

    @Autowired
    private RestHighLevelClient esClient;

    public boolean addPost(Post postPO) throws IOException {
        IndexRequest indexRequest = new IndexRequest("arrietty")
                .source("user", "kimchy",
                        "message", "trying out Elasticsearch");

        IndexResponse indexResponse = esClient.index(indexRequest,RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        return true;
    }

    public static void main(String[] args)  throws IOException{
        RestHighLevelClient esClient =  new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));
        IndexRequest indexRequest = new IndexRequest("arrietty")
                .source("user", "kimchy",
                        "message", "trying out Elasticsearch");

        IndexResponse indexResponse = esClient.index(indexRequest,RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
    }
}
