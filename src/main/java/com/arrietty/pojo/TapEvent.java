package com.arrietty.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class TapEvent {
    private Long senderId;
    private Long receiverId;
    private Long advertisementId;
    private Date createTime;
}
