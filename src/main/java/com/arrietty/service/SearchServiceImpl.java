package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class SearchServiceImpl {

    public static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Value("${elasticsearch.page-size}")
    private Integer PAGE_SIZE;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ProfileServiceImpl profileService;

    public List<SearchResultItem> handleSearchRequest(PostSearchRequestPO requestPO) throws LogicException {
        checkSearchRequest(requestPO);

        List<SearchResultItem> result = new LinkedList<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("advertisement").types("_doc");

        BoolQueryBuilder queryFilter = QueryBuilders.boolQuery();



        if("textbook".equals(requestPO.getAdType())){
//            queryFilter.filter(QueryBuilders.termQuery("is_textbook", true));
            queryFilter.filter(QueryBuilders.matchPhraseQuery("textbook_tag.title", requestPO.getKeyword()));
        }
        else {
            //  other item 默认匹配ad title? 细节待定
//            queryFilter.filter(QueryBuilders.termQuery("is_textbook", false));
            queryFilter.filter(QueryBuilders.matchPhraseQuery("adTitle", requestPO.getKeyword()));
        }

        if(requestPO.getMinPrice()!=null){
            queryFilter.filter(QueryBuilders.rangeQuery("price").gte(requestPO.getMinPrice()).lte(requestPO.getMaxPrice()));
        }


        if("other".equals(requestPO.getAdType()) && requestPO.getTag()!=null){
            queryFilter.filter(QueryBuilders.termQuery("other_tag", requestPO.getTag()));
        }


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(requestPO.getPageNum()==null?0:requestPO.getPageNum()*PAGE_SIZE);
        searchSourceBuilder.size(PAGE_SIZE);
        searchSourceBuilder.query(queryFilter);

        if("asc".equals(requestPO.getPriceOrder())){
            searchSourceBuilder.sort("price", SortOrder.ASC);
        }
        else if("desc".equals(requestPO.getPriceOrder())){
            searchSourceBuilder.sort("price", SortOrder.DESC);
        }

        searchSourceBuilder.sort("create_time",SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        try{
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Set<Long> currentUserTappedAdIds = redisService.getCurrentUserTappedAdIds();
            SearchHit[] hits = response.getHits().getHits();
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
            for (SearchHit hit : hits) {
                ESAdvertisementPO po = gson.fromJson(hit.getSourceAsString(), ESAdvertisementPO.class);
                SearchResultItem searchResultItem = null;
                if(currentUserTappedAdIds.contains((long) hit.docId())){
                    searchResultItem = mapDocumentToSearchResultItem(po,true);
                }
                else {
                    searchResultItem = mapDocumentToSearchResultItem(po,false);
                }

                result.add(searchResultItem);
            }
        }
        catch (Exception e){
            // TODO: proper search error handling
            e.printStackTrace();
        }

        return result;
    }


    private void checkSearchRequest(PostSearchRequestPO requestPO) throws LogicException {
        if(requestPO==null ||
            requestPO.getAdType()==null ||
                requestPO.getKeyword()==null ||
                requestPO.getKeyword().length()==0 ||
                (requestPO.getMinPrice()==null)!=(requestPO.getMaxPrice()==null) ||
                (requestPO.getMinPrice()!=null && requestPO.getMinPrice().compareTo(requestPO.getMaxPrice())>0)
        ){
            throw new LogicException(ErrorCode.INVALID_SEARCH_PARAM, "Invalid search parameters.");
        }
    }

    private SearchResultItem mapDocumentToSearchResultItem(ESAdvertisementPO po, boolean userInfoUnlocked){
        SearchResultItem item = new SearchResultItem();
        item.setAdTitle(po.getAdTitle());
        item.setImageIds(po.getImageIds());
        item.setPrice(po.getPrice());
        item.setComment(po.getComment());

        LocalDate localDate = LocalDate.parse(po.getCreateTime());
        item.setCreateTime(Date.from(localDate.atStartOfDay(ZONE_ID).toInstant()));

        if(po.getIsTextbook()!=null && po.getIsTextbook() && po.getTextbookTag()!=null){
            item.setAdType("textbook");
            item.setTextbookTitle(po.getTextbookTag().getTitle());
            item.setIsbn(po.getTextbookTag().getIsbn());
            item.setAuthor(po.getTextbookTag().getAuthor());
            item.setPublisher(po.getTextbookTag().getPublisher());
            item.setEdition(po.getTextbookTag().getEdition());
            item.setOriginalPrice(po.getTextbookTag().getOriginalPrice());
            if(po.getTextbookTag().getRelatedCourse()!=null){
                item.setRelatedCourse(po.getTextbookTag().getRelatedCourse().getCourseName());
            }

        }
        else {
            item.setAdType("other");
            item.setOtherTag(po.getOtherTag());
        }

        if(userInfoUnlocked){
            ProfilePO profilePO = profileService.getUserProfile(po.getUserId());
            item.setUsername(profilePO.getUsername());
            item.setUserNetId(profilePO.getNetId());
            item.setUserAvatarImageId(profilePO.getAvatarImageId());
        }

        return item;
    }
}
