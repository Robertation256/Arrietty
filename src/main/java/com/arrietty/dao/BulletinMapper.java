package com.arrietty.dao;

import com.arrietty.entity.Bulletin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulletinMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bulletin
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bulletin
     *
     * @mbg.generated
     */
    int insert(Bulletin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bulletin
     *
     * @mbg.generated
     */
    Bulletin selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bulletin
     *
     * @mbg.generated
     */
    List<Bulletin> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bulletin
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Bulletin record);
}