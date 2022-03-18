package com.arrietty.pojo;


import com.arrietty.entity.Advertisement;
import lombok.Data;

import java.util.Date;

@Data
public class AdvertisementEvent {
    private String eventType;
    private Date timestamp;
    private Advertisement advertisement;
}
