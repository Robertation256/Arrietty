package com.arrietty.pojo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PostAdvertisementRequestPO {
    private Long id;
    private Boolean isTextbook;
    private Long tagId;
    private List<MultipartFile> images;
    private BigDecimal price;
    private String comment;
}
