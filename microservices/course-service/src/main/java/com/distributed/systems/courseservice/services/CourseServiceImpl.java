package com.distributed.systems.courseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseServiceImpl implements CourseService{

    private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    public CourseServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Course getCourse(int courseId) {
        LOG.info("/course return the found course with courseId={}", courseId);
        LOG.debug("/course return the found course with courseId={}", courseId);
        if(courseId < 1) throw new InvalidInputException("Invalid courseId: "+courseId);
        if(courseId == 13) throw  new NotFoundException("No course with id: "+courseId);
        return new Course(courseId, "Data Science with Python", 5, "$", serviceUtil.getServiceAddress());
    }
}
