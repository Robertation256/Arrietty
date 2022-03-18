package com.arrietty.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdvertisementResponsePO {
    private Long id;
    private Boolean isTextbook;
    private Long tagId;
    private String imageIds;
    private BigDecimal price;
    private String comment;
    private Integer numberOfTaps;
    private Date createTime;
}
