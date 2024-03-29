package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.*;
import com.arrietty.utils.session.SessionContext;
import com.google.gson.Gson;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class SearchServiceImpl {
    private static  final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Value("${elasticsearch.page-size}")
    private int PAGE_SIZE;

    @Value("${elasticsearch.max-suggestion-size}")
    private int MAX_SUGGESTION_SIZE;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private AdvertisementServiceImpl advertisementService;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private TapServiceImpl tapService;

    public List<SearchResultItem> getMyAdvertisement() throws LogicException {
        List<SearchResultItem> result = new LinkedList<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("advertisement").types("_doc");

        BoolQueryBuilder queryFilter = QueryBuilders.boolQuery();
        queryFilter.filter(QueryBuilders.termQuery("user_id", SessionContext.getUserId().toString()));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryFilter);
        searchRequest.source(searchSourceBuilder);

        try{
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                ESAdvertisementPO po = new Gson().fromJson(hit.getSourceAsString(), ESAdvertisementPO.class);
                SearchResultItem searchResultItem = mapDocumentToSearchResultItem(po,false);
                Long adId = Long.parseLong(hit.getId());
                searchResultItem.setNumberOfTaps(tapService.getNumberOfTaps(adId));
                searchResultItem.setId(adId);
                result.add(searchResultItem);
            }
        }
        catch (IOException e){
            logger.error("failed to fetch current user advertisements", e);
        }

        return result;
    }


    public List<SearchResultItem> handleSearchRequest(PostSearchRequestPO requestPO) throws LogicException {
        checkSearchRequest(requestPO);

        List<SearchResultItem> result = new LinkedList<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("advertisement");

        BoolQueryBuilder queryFilter = QueryBuilders.boolQuery();
        queryFilter.filter(QueryBuilders.termQuery("is_textbook", "textbook".equals(requestPO.getAdType())));

        // textbook match by textbook title, other item match by ad title
        if(requestPO.getKeyword()!=null && requestPO.getKeyword().length()>0){
            if("textbook".equals(requestPO.getAdType())){
                queryFilter.must(QueryBuilders.multiMatchQuery(requestPO.getKeyword(),"textbook_tag.title", "ad_title", "comment")
                        .operator(Operator.OR)
                        .fuzziness(Fuzziness.AUTO).prefixLength(3));
            }
            else {
                queryFilter.must(QueryBuilders.multiMatchQuery(requestPO.getKeyword(),"ad_title", "comment")
                        .operator(Operator.OR)
                        .fuzziness(Fuzziness.AUTO).prefixLength(3));
            }
        }
        
        if(requestPO.getMinPrice()!=null){
            queryFilter.filter(QueryBuilders.rangeQuery("price").gte(requestPO.getMinPrice()).lte(requestPO.getMaxPrice()));
        }



        if("other".equals(requestPO.getAdType()) && requestPO.getTag()!=null){
            String[] tags = requestPO.getTag().split(",");
            if(tags.length>10){
                throw new LogicException(ErrorCode.INVALID_SEARCH_PARAM, "Tag length exceeds maximum size.");
            }
            BoolQueryBuilder tagFilter = QueryBuilders.boolQuery();
            for (String tag : tags){
                tagFilter.should(QueryBuilders.termQuery("other_tag", tag));
            }
            queryFilter.filter(tagFilter);
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
            Set<String> currentUserTappedAdIds = redisService.getUserTappedAdIds(SessionContext.getUserId());
            Set<String> markedAdIds = favoriteService.getCurrentUserMarkedAdIds();
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                ESAdvertisementPO po = new Gson().fromJson(hit.getSourceAsString(), ESAdvertisementPO.class);
                SearchResultItem searchResultItem = null;
                Long adId = Long.parseLong(hit.getId());

                // show owner info if the ad belongs to current user or is unlocked by current user
                if(currentUserTappedAdIds.contains(hit.getId()) ||
                    advertisementService.isCurrentUserAd(adId)
                ){
                    searchResultItem = mapDocumentToSearchResultItem(po,true);
                }
                else {
                    searchResultItem = mapDocumentToSearchResultItem(po,false);
                }

                if(markedAdIds.contains(hit.getId())){
                    searchResultItem.setIsMarked(true);
                }
                else{
                    searchResultItem.setIsMarked(false);
                }

                searchResultItem.setNumberOfTaps(tapService.getNumberOfTaps(adId));
                searchResultItem.setId(adId);
                result.add(searchResultItem);
            }
        }
        catch (Exception e){
            logger.error("[fetch advertisement search result failed]", e);
        }

        redisService.incrementSearchRequestNum();
        return result;
    }


    public List<String> handleKeywordSuggestion(String type, String keyword) throws  LogicException {
        List<String> result = new LinkedList<>();
        if(keyword==null || keyword.length()==0){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Keyword is empty.");
        }

        if("textbook".equals(type)){
            keyword = "T:"+keyword;
        }
        else if ("other".equals(type)){
            keyword = "O:"+keyword;
        }
        else {
            logger.warn("[Keyword suggestion failed] invalid suggestion type");
            return result;
        }

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("advertisement");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("suggest")
                .prefix(keyword)
                .size(MAX_SUGGESTION_SIZE)
                .skipDuplicates(true);
        suggestBuilder.addSuggestion("advertisement_suggest", completionSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);


        try{
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion completionSuggestion = suggest.getSuggestion("advertisement_suggest");
            for (CompletionSuggestion.Entry entry : completionSuggestion.getEntries()) {
                for (CompletionSuggestion.Entry.Option option : entry) {
                    String suggestText = option.getText().string();
                    result.add(suggestText.substring(2));
                }
            }
        }
        catch (Exception e){
            logger.error("[fetch suggestion failed] ", e);
        }
        return result;
    }

    private void checkSearchRequest(PostSearchRequestPO requestPO) throws LogicException {
        if(requestPO==null ||
            requestPO.getAdType()==null ||
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

        LocalDateTime localDateTme = LocalDateTime.parse(po.getCreateTime(), fmt);
        item.setCreateTime(Date.from(localDateTme.atZone(ZONE_ID).toInstant()));

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
