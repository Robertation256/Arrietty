package com.arrietty.service;


import com.arrietty.consts.ErrorCode;
import com.arrietty.dao.CourseMapper;
import com.arrietty.entity.Course;
import com.arrietty.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl {
    private static final String lock = "courseWriteLock";

    @Autowired
    private CourseMapper courseMapper;

    public List<Course> getCourseById(Long id){

        // id is null, return all courses
        if(id==null){
            return courseMapper.selectAll();
        }
        else{
            List<Course> courses = new ArrayList<>(1);
            Course course = courseMapper.selectByPrimaryKey(id);
            if(course==null){
                throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Course does not exist.");
            }
            courses.add(course);
            return courses;
        }
    }


    public Course handleCourseEdit(String action, Course course) throws LogicException {
        if("update".equals(action)){
            return handleCourseUpdate(course);
        }
        else if("delete".equals(action)){
            handleCourseDelete(course);
        }
        else {
            throw new LogicException(ErrorCode.INVALID_URL_PARAM, "Invalid action type.");
        }

        return null;
    }

    private Course handleCourseUpdate(Course course) throws LogicException{

        if(course.getCourseCode()==null || course.getCourseName()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Course code or name cannot be empty.");
        }

        // insert
        if(course.getId()==null){
            //check for duplicate course by courseCode
            synchronized (lock){
                Course duplicateCourse = courseMapper.selectByCourseCode(course.getCourseCode());
                if(duplicateCourse!=null){
                    throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Duplicate course exists.");
                }
                courseMapper.insert(course);
                return course;
            }
        }
        //update
        else{
            int recordUpdatedAmount = courseMapper.updateByPrimaryKey(course);
            if(recordUpdatedAmount==0){
                throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Course does not exist.");
            }
        }

        return null;
    }

    private void handleCourseDelete(Course course) throws LogicException{
        if(course.getId()==null){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Course id is empty.");
        }
        int recordDeletedAmount = courseMapper.deleteByPrimaryKey(course.getId());
        if(recordDeletedAmount==0){
            throw new LogicException(ErrorCode.INVALID_REQUEST_BODY, "Course does not exist.");
        }
    }
}
