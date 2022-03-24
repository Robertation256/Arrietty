package com.arrietty.pojo;


import lombok.Data;

@Data
public class PostSearchRequestPO {
    private String adType;
    private String keyword;
    private String priceOrder;
    private Integer minPrice;
    private Integer maxPrice;
    private String tag;
    private Integer pageNum;

}
