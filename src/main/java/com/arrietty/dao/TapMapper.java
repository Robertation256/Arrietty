package com.arrietty.dao;

import com.arrietty.entity.Tap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TapMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tap
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tap
     *
     * @mbg.generated
     */
    int insert(Tap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tap
     *
     * @mbg.generated
     */
    Tap selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tap
     *
     * @mbg.generated
     */
    List<Tap> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tap
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Tap record);

    List<Long> selectTappedAdIdsBySenderId(Long senderId);

    List<Tap> selectByReceiverId(Long receiverId);
}