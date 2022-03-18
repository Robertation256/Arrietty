package com.arrietty.pojo;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ESTextbookTagPO {

    private String title;

    private String isbn;

    private String author;

    private String publisher;

    private String edition;

    @SerializedName("original_price")
    private BigDecimal originalPrice;

    @SerializedName("related_course")
    private ESRelatedCoursePO relatedCourse;
}
