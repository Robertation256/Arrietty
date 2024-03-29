package com.arrietty.dao;

import com.arrietty.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table favorite
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table favorite
     *
     * @mbg.generated
     */
    int insert(Favorite record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table favorite
     *
     * @mbg.generated
     */
    Favorite selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table favorite
     *
     * @mbg.generated
     */
    List<Favorite> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table favorite
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Favorite record);

    int deleteByUserIdAndAdId(Long userId, Long adId);

    List<Favorite> selectByUserId(Long userId);
}