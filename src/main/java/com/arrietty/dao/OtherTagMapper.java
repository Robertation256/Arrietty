package com.arrietty.dao;

import com.arrietty.entity.OtherTag;
import java.util.List;

public interface OtherTagMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table other_tag
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table other_tag
     *
     * @mbg.generated
     */
    int insert(OtherTag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table other_tag
     *
     * @mbg.generated
     */
    OtherTag selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table other_tag
     *
     * @mbg.generated
     */
    List<OtherTag> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table other_tag
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OtherTag record);
}