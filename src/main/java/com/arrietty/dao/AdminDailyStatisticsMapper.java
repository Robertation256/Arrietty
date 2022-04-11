package com.arrietty.dao;

import com.arrietty.entity.AdminDailyStatistics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminDailyStatisticsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    int insert(AdminDailyStatistics record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    AdminDailyStatistics selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    List<AdminDailyStatistics> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdminDailyStatistics record);
}