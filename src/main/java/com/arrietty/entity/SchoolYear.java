package com.arrietty.entity;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/25 17:37
 */
public class SchoolYear {

    public static final Integer FRESHMAN = 1;
    public static final Integer SOPHOMORE = 2;
    public static final Integer JUNIOR = 3;
    public static final Integer SENIOR = 4;
    public static final List<Integer> VALID_YEARS = Arrays.asList(1,2,3,4);

    public static Boolean isValidSchoolYear(Integer schoolYear) {
        return VALID_YEARS.contains(schoolYear);
    }
}
