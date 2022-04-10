package com.arrietty.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SearchResultItem {
    private Long id;
    private String username;
    private String userNetId;
    private Long userAvatarImageId;
    private String adType;
    private String adTitle;
    private String textbookTitle;
    private String isbn;
    private String author;
    private String publisher;
    private String edition;
    private BigDecimal originalPrice;
    private String relatedCourse;
    private String otherTag;
    private String imageIds;
    private BigDecimal price;
    private String comment;
    private Date createTime;
    private Boolean isMarked;
    private Integer numberOfTaps;
}
