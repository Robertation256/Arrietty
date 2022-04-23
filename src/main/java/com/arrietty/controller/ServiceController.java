package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.annotations.RateLimit;
import com.arrietty.consts.*;
import com.arrietty.entity.*;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.*;
import com.arrietty.service.*;
import com.arrietty.utils.response.Response;
import com.google.gson.Gson;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@Controller
public class ServiceController {
    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private CourseServiceImpl courseService;

    @Autowired
    private TextbookTagServiceImpl textbookTagService;

    @Autowired
    private OtherTagServiceImpl otherTagService;

    @Autowired
    private AdvertisementServiceImpl advertisementService;

    @Autowired
    private AmqpTemplate mqTemplate;

    @Autowired
    private SearchServiceImpl searchService;

    @Autowired
    private TapServiceImpl tapService;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private BulletinServiceImpl bulletinService;

    @Autowired
    private AdminServiceImpl adminService;

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/")
    public ModelAndView root(){
        return new ModelAndView("index.html");
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/home")
    public ModelAndView userHome(){
        return new ModelAndView("index.html");
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/myPosts")
    public ModelAndView userPosts(){
        return new ModelAndView("index.html");
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/favorite")
    public ModelAndView userFavorite(){
        return new ModelAndView("index.html");
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/notification")
    public ModelAndView userNotification(){
        return new ModelAndView("index.html");
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.ADMIN, redirectPolicy = RedirectPolicyEnum.REDIRECT)
    @GetMapping("/admin")
    public ModelAndView userAdmin(){
        return new ModelAndView("index.html");
    }


    // 修改用户本人profile
    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/profile")
    public String postProfile(@RequestBody ProfilePO profilePO){
        Response<ProfilePO> response;
        ProfilePO res = profileService.updateUserProfile(profilePO);
        if(res!=null){
            response = Response.buildSuccessResponse(ProfilePO.class, res);
        }
        else{
            response = Response.buildFailedResponse(ErrorCode.PROFILE_EDIT_ERROR, "update profile failed");
        }

        return new Gson().toJson(response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode=AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/profile")
    public String getProfile(@RequestParam("userId") Long userId){
        Response<ProfilePO> response;
        ProfilePO profilePO = profileService.getUserProfile(userId);
        if(profilePO!=null){
            response = Response.buildSuccessResponse(ProfilePO.class, profilePO);
        }
        else{
            response = Response.buildFailedResponse();
        }

        return new Gson().toJson(response);
    }



    // handles user avatar image update
    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam("file") MultipartFile uploadedFile) throws LogicException {
        imageService.updateAvatar(uploadedFile);
        return new Gson().toJson(Response.buildSuccessResponse());
    }

    //return user avatar image
    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @GetMapping("/avatar")
    public void getAvatar(HttpServletResponse response) throws LogicException{
        imageService.getAvatar(response);
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @RateLimit(rateLimit = RateLimitPolicy.STANDARD)
    @GetMapping("/image")
    public void getImage(@RequestParam("id") Long id, HttpServletResponse response) throws LogicException{
        imageService.getImage(id, response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/course")
    public String getCourse(@RequestParam("id") Long id) throws LogicException{
        List<Course> courses = courseService.getCourseById(id);
        Response<List<Course>> response = Response.buildSuccessResponse(Course.class, courses);
        return new Gson().toJson(response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/course")
    public String postCourse(@RequestParam("action") String action, @RequestBody Course course) throws LogicException{
        Course ret = courseService.handleCourseEdit(action, course);
        Response<Course> response = Response.buildSuccessResponse(Course.class, ret);
        return new Gson().toJson(response);
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/textbook")
    public String getTextbookTag(@RequestParam("id") Long id) throws LogicException{
        List<TextbookTag> textbookTags = textbookTagService.getTextbookTagById(id);
        Response<List<TextbookTag>> response = Response.buildSuccessResponse(TextbookTag.class, textbookTags);
        return new Gson().toJson(response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/textbook")
    public String postTextbookTag(@RequestParam("action") String action, @RequestBody TextbookTag textbookTag) throws LogicException{
        TextbookTag ret = textbookTagService.handleTextbookTagEdit(action, textbookTag);
        Response<TextbookTag> response = Response.buildSuccessResponse(TextbookTag.class, ret);
        return new Gson().toJson(response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/otherTag")
    public String getOtherTag(@RequestParam("id") Long id) throws LogicException{
        List<OtherTag> otherTags = otherTagService.getOtherTagById(id);
        Response<List<OtherTag>> response = Response.buildSuccessResponse(OtherTag.class, otherTags);
        return new Gson().toJson(response);
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/otherTag")
    public String postOtherTag(@RequestParam("action") String action, @RequestBody OtherTag otherTag) throws LogicException{
        OtherTag ret = otherTagService.handleOtherTagEdit(action, otherTag);
        Response<OtherTag> response = Response.buildSuccessResponse(OtherTag.class, ret);
        return new Gson().toJson(response);
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/advertisement")
    public String postAdvertisement(@RequestParam("action") String action, @ModelAttribute PostAdvertisementRequestPO requestPO) throws LogicException {
        AdvertisementResponsePO advertisementResponsePO = advertisementService.handlePostAdvertisement(action, requestPO);
        return new Gson().toJson(Response.buildSuccessResponse(AdvertisementResponsePO.class, advertisementResponsePO));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/myAdvertisement")
    public String getMyAdvertisement() throws LogicException {
        List<SearchResultItem> result = searchService.getMyAdvertisement();
        return new Gson().toJson(Response.buildSuccessResponse(SearchResultItem.class, result));
    }




    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/search")
    public String postSearch(@RequestBody PostSearchRequestPO requestPO) throws LogicException {
        List<SearchResultItem> results = searchService.handleSearchRequest(requestPO);
        return new Gson().toJson(Response.buildSuccessResponse(SearchResultItem.class, results));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/suggest")
    public String postSuggest(@RequestParam("type") String type, @RequestParam("keyword") String keyword) throws LogicException {
        List<String> result = searchService.handleKeywordSuggestion(type, keyword);
        return new Gson().toJson(Response.buildSuccessResponse(String.class, result));
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/lastModified")
    public String getLastModified() throws LogicException {
        Date result = advertisementService.getLastModified();
        return new Gson().toJson(Response.buildSuccessResponse(Date.class, result));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/tap")
    public String getTap(@RequestParam("id") Long id ) throws LogicException {
        TapResponsePO tapResponsePO = tapService.handleTap(id);
        return new Gson().toJson(Response.buildSuccessResponse(TapResponsePO.class, tapResponsePO));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/getNotification")
    public String getNotification() throws LogicException {
        List<TapPO> result = tapService.getCurrentUserNotifications();
        return new Gson().toJson(Response.buildSuccessResponse(TapPO.class, result));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/hasNew")
    public String getHasNew() throws LogicException {
        Boolean result = tapService.hasNew();
        return new Gson().toJson(Response.buildSuccessResponse(Boolean.class, result));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/mark")
    public String getMark(@RequestParam("id") Long id, @RequestParam("status") String status) throws LogicException {
        favoriteService.handleMarkAction(status, id);
        return new Gson().toJson(Response.buildSuccessResponse());
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/getFavorite")
    public String getFavorite() throws LogicException {
        List<SearchResultItem> result =  favoriteService.handleGetFavorite();
        return new Gson().toJson(Response.buildSuccessResponse(SearchResultItem.class, result));
    }

    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/bulletin")
    public String getBulletin() throws LogicException {
        List<Bulletin> bulletins = bulletinService.getBulletin();
        return new Gson().toJson(Response.buildSuccessResponse(Bulletin.class, bulletins));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/bulletin")
    public String postBulletin(@RequestParam("action") String action, @RequestBody Bulletin bulletin) throws LogicException {
        Bulletin result = bulletinService.handlePostBulletin(action, bulletin);
        if(result==null){
            return new Gson().toJson(Response.buildSuccessResponse());
        }
        return new Gson().toJson(Response.buildSuccessResponse(Bulletin.class, result));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @GetMapping("/blacklist")
    public String getBlacklist() throws LogicException {
        List<String> result = adminService.getBlacklistedUserNetIds();
        return new Gson().toJson(Response.buildSuccessResponse(String.class, result));
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/updateBlacklist")
    public String updateBlacklist(@RequestParam("action") String action, @RequestParam("netId") String netId) throws LogicException {
        adminService.updateBlacklist(action,netId);
        return new Gson().toJson(Response.buildSuccessResponse());
    }


    @RateLimit(rateLimit = RateLimitPolicy.STRICT)
    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @GetMapping("/adminStatistics")
    public String getAdminStatistics(){
        List<AdminDailyStatistics> result = adminService.getAdminStatistics();
        return new Gson().toJson(Response.buildSuccessResponse(AdminDailyStatistics.class, result));
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Auth(authMode = AuthModeEnum.REGULAR)
    @GetMapping("/test")
    @ResponseBody
    public String mqTest() throws Exception{
//        PostAdvertisementRequestPO po = new PostAdvertisementRequestPO();
//        po.setId(12L);
//        advertisementService.handlePostAdvertisement("delete",po);

//        System.out.println(searchService.getMyAdvertisement());


        Set<String> result = redisTemplate.opsForSet().members(RedisKey.VALID_TEXTBOOK_TAG_ID_SET);
        Long l = 11L;
        Boolean bool1 = redisTemplate.opsForSet().isMember(RedisKey.VALID_TEXTBOOK_TAG_ID_SET, l.toString());
        Boolean bool2 = redisTemplate.opsForSet().isMember(RedisKey.VALID_TEXTBOOK_TAG_ID_SET, l);
        return "hii";

//        List<String> result = searchService.handleKeywordSuggestion("other","a");
//        System.out.println(result);
//        return "hii";

//        PostSearchRequestPO testPo = new PostSearchRequestPO();
//        testPo.setAdType("other");
//        testPo.setTag("electronics");
//
//        List<SearchResultItem> ret = searchService.handleSearchRequest(testPo);
//        System.out.println(ret);
//        return "hii";


//        BulkRequest request = new BulkRequest();
//        Map<String,String> dataMap = new HashMap<>();
//        dataMap.put("content","hello world");
//        request.add(new IndexRequest("arrietty","test").id("666")
//                .opType("create").source(dataMap,XContentType.JSON));
//        BulkResponse bulkResponse =  esClient.bulk(request, RequestOptions.DEFAULT);
        //return bulkResponse.getItems()[0].toString();


//        List<TapPO> test = tapService.getCurrentUserNotifications();
//        System.out.println(test);
//
//        return "hii";

//        List<Bulletin> bulletins = bulletinService.getBulletin();
//        return new Gson().toJson(Response.buildSuccessResponse(Bulletin.class, bulletins));
    }
}
