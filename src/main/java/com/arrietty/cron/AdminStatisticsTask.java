package com.arrietty.cron;


import com.arrietty.entity.AdminDailyStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
public class AdminStatisticsTask {

//    @Autowired
//    private

//    @Scheduled(cron = "0 0 24 * * ?")
//    public void processAdminStatistics(){
//        AdminDailyStatistics statistics = new AdminDailyStatistics();
//
//        statistics.setTotalAdNum();
//
//    }
}
