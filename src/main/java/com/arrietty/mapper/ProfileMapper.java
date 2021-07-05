package com.arrietty.mapper;


import com.arrietty.entity.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface ProfileMapper {

    public Profile queryProfileByUserId(@Param("userId") Long userId);

}
