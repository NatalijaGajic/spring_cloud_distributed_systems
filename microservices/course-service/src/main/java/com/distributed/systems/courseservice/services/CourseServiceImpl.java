package com.distributed.systems.courseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
import com.distributed.systems.courseservice.persistence.CourseEntity;
import com.distributed.systems.courseservice.persistence.CourseRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseServiceImpl implements CourseService{

    private final ServiceUtil serviceUtil;

    private final CourseRepository repository;
    private final CourseMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    public CourseServiceImpl(ServiceUtil serviceUtil, CourseRepository repository, CourseMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Course createProduct(Course body) {
       try{
           CourseEntity entity = mapper.apiToEntity(body);
           CourseEntity newEntity = repository.save(entity);

           LOG.debug("createCourse: entity created for courseId: {}", body.getCourseId());
           return mapper.entityToApi(newEntity);

       }catch (DuplicateKeyException dk){
           throw new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId());
       }
    }

    @Override
    public Course getCourse(int courseId) {
        if(courseId < 1) throw new InvalidInputException("Invalid courseId: "+courseId);

        CourseEntity entity = repository.findByCourseId(courseId)
                        .orElseThrow(() -> new NotFoundException("No course found for courseId: " + courseId));
        Course response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getCourse: found courseId: {}", courseId);
        return response;


    }

    @Override
    public void deleteCourse(int courseId) {
        LOG.debug("deleteCourse: tries to delete an entity with courseId: {}", courseId);
        repository.findByCourseId(courseId).ifPresent(e -> repository.delete(e));
    }
}
