package com.arrietty.cron;


import com.arrietty.dao.AdminDailyStatisticsMapper;
import com.arrietty.dao.AdvertisementMapper;
import com.arrietty.dao.TapMapper;
import com.arrietty.dao.UserMapper;
import com.arrietty.entity.AdminDailyStatistics;
import com.arrietty.entity.Advertisement;
import com.arrietty.service.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@Configurable
@EnableScheduling
public class AdminStatisticsTask {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private TapMapper tapMapper;

    @Autowired
    private AdminDailyStatisticsMapper adminDailyStatisticsMapper;

    @Autowired
    private RedisServiceImpl redisService;

    @Scheduled(cron = "0 24 10 * * ?")
    public void processAdminStatistics(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, -1);
        Date start = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        Date end = calendar.getTime();

        AdminDailyStatistics statistics = new AdminDailyStatistics();
        statistics.setTotalUserNum(userMapper.getTotalUserCount());
        statistics.setLoginUserNum(userMapper.getLoginUserCount(start, end));

        statistics.setTotalAdNum(advertisementMapper.getTotalAdNum());
        statistics.setAdUploadNum(redisService.getUserAdUploadNum());
        statistics.setAdEditNum(redisService.getUserAdUpdateNum());
        statistics.setAdDeleteNum(redisService.getUserAdDeleteNum());

        statistics.setTapRequestNum(tapMapper.getDailyUserTapNum(start, end));

        statistics.setMarkRequestNum(redisService.getUsermarkNum());
        statistics.setUnmarkRequestNum(redisService.getUserUnmarkNum());

        statistics.setSearchRequestNum(redisService.getSearchRequestNum());

        statistics.setDate(start);

        adminDailyStatisticsMapper.insert(statistics);
    }
}
