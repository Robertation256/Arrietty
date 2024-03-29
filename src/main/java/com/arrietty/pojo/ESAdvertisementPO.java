package com.arrietty.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ESAdvertisementPO {
    @SerializedName("ad_title")
    private String adTitle;

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("is_textbook")
    private Boolean isTextbook;

    @SerializedName("textbook_tag")
    private ESTextbookTagPO textbookTag;

    @SerializedName("other_tag")
    private String otherTag;

    @SerializedName("image_ids")
    private String imageIds;

    private BigDecimal price;

    private String comment;

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("suggest")
    private String suggest;
}
