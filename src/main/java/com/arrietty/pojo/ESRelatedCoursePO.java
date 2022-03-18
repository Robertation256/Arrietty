package com.arrietty.pojo;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ESRelatedCoursePO {

    @SerializedName("course_code")
    private String courseCode;

    @SerializedName("course_name")
    private String courseName;

    private String subject;
}
