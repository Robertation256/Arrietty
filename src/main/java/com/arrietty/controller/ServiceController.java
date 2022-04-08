package com.arrietty.controller;

import com.arrietty.annotations.Auth;
import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.ErrorCode;
import com.arrietty.entity.Course;
import com.arrietty.entity.OtherTag;
import com.arrietty.entity.TextbookTag;
import com.arrietty.exception.LogicException;
import com.arrietty.pojo.*;
import com.arrietty.service.*;
import com.arrietty.utils.response.Response;
import com.google.gson.Gson;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.util.List;



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



    @Auth(authMode=AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/home")
    public ModelAndView userHome(){
        return new ModelAndView("index.html");
    }


    // 修改用户本人profile
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
    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam("file") MultipartFile uploadedFile) throws LogicException {
        imageService.updateAvatar(uploadedFile);
        return new Gson().toJson(Response.buildSuccessResponse());
    }

    //return user avatar image
    @Auth(authMode = AuthModeEnum.REGULAR)
    @GetMapping("/avatar")
    public void getAvatar(HttpServletResponse response) throws LogicException{
        imageService.getAvatar(response);
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @GetMapping("/image")
    public void getImage(@RequestParam("id") Long id, HttpServletResponse response) throws LogicException{
        imageService.getImage(id, response);
    }



    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/course")
    public String getCourse(@RequestParam("id") Long id) throws LogicException{
        List<Course> courses = courseService.getCourseById(id);
        Response<List<Course>> response = Response.buildSuccessResponse(Course.class, courses);
        return new Gson().toJson(response);
    }


    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/course")
    public String postCourse(@RequestParam("action") String action, @RequestBody Course course) throws LogicException{
        Course ret = courseService.handleCourseEdit(action, course);
        Response<Course> response = Response.buildSuccessResponse(Course.class, ret);
        return new Gson().toJson(response);
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/textbook")
    public String getTextbookTag(@RequestParam("id") Long id) throws LogicException{
        List<TextbookTag> textbookTags = textbookTagService.getTextbookTagById(id);
        Response<List<TextbookTag>> response = Response.buildSuccessResponse(TextbookTag.class, textbookTags);
        return new Gson().toJson(response);
    }


    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/textbook")
    public String postTextbookTag(@RequestParam("action") String action, @RequestBody TextbookTag textbookTag) throws LogicException{
        TextbookTag ret = textbookTagService.handleTextbookTagEdit(action, textbookTag);
        Response<TextbookTag> response = Response.buildSuccessResponse(TextbookTag.class, ret);
        return new Gson().toJson(response);
    }


    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/otherTag")
    public String getOtherTag(@RequestParam("id") Long id) throws LogicException{
        List<OtherTag> otherTags = otherTagService.getOtherTagById(id);
        Response<List<OtherTag>> response = Response.buildSuccessResponse(OtherTag.class, otherTags);
        return new Gson().toJson(response);
    }


    @Auth(authMode = AuthModeEnum.ADMIN)
    @ResponseBody
    @PostMapping("/otherTag")
    public String postOtherTag(@RequestParam("action") String action, @RequestBody OtherTag otherTag) throws LogicException{
        OtherTag ret = otherTagService.handleOtherTagEdit(action, otherTag);
        Response<OtherTag> response = Response.buildSuccessResponse(OtherTag.class, ret);
        return new Gson().toJson(response);
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/advertisement")
    public String postAdvertisement(@RequestParam("action") String action, @ModelAttribute PostAdvertisementRequestPO requestPO) throws LogicException {
        AdvertisementResponsePO advertisementResponsePO = advertisementService.handlePostAdvertisement(action, requestPO);
        return new Gson().toJson(Response.buildSuccessResponse(AdvertisementResponsePO.class, advertisementResponsePO));
    }


    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/search")
    public String postSearch(@RequestBody PostSearchRequestPO requestPO) throws LogicException {
        List<SearchResultItem> results = searchService.handleSearchRequest(requestPO);
        return new Gson().toJson(Response.buildSuccessResponse(SearchResultItem.class, results));
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/suggest")
    public String postSuggest(@RequestParam("type") String type, @RequestParam("keyword") String keyword) throws LogicException {
        List<String> result = searchService.handleKeywordSuggestion(type, keyword);
        return new Gson().toJson(Response.buildSuccessResponse(String.class, result));
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @PostMapping("/lastModified")
    public String getLastModified(@RequestParam("target") String target ) throws LogicException {
        Integer id = redisServiceImpl.getVersionId(target);

        if(id==null){
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid target");
        }

        return new Gson().toJson(Response.buildSuccessResponse(Integer.class, id));
    }

    @Auth(authMode = AuthModeEnum.REGULAR)
    @ResponseBody
    @GetMapping("/tap")
    public String getTap(@RequestParam("id") Long id ) throws LogicException {
        TapResponsePO tapResponsePO = tapService.handleTap(id);
        return new Gson().toJson(Response.buildSuccessResponse(TapResponsePO.class, tapResponsePO));
    }





    @GetMapping("/test")
    @ResponseBody
    public String mqTest() throws Exception{

        List<String> result = searchService.handleKeywordSuggestion("other","a");
        System.out.println(result);
        return "hii";

//        PostSearchRequestPO testPo = new PostSearchRequestPO();
//        testPo.setAdType("textbook");
//        testPo.setKeyword("test1");
//        testPo.setMinPrice(700);
//        testPo.setMaxPrice(800);
//        searchService.handleSearchRequest(testPo);
//        return "hii";


//        BulkRequest request = new BulkRequest();
//        Map<String,String> dataMap = new HashMap<>();
//        dataMap.put("content","hello world");
//        request.add(new IndexRequest("arrietty","test").id("666")
//                .opType("create").source(dataMap,XContentType.JSON));
//        BulkResponse bulkResponse =  esClient.bulk(request, RequestOptions.DEFAULT);






        //return bulkResponse.getItems()[0].toString();


    }
}
