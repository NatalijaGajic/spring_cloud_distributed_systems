package com.distributed.systems.courseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseServiceImpl implements CourseService{
    @Override
    public Course getCourse(int courseId) {
        return null;
    }
}
