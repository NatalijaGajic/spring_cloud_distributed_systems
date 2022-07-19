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
import reactor.core.publisher.Mono;

import java.util.Random;

import static reactor.core.publisher.Mono.error;

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
    public Course createCourse(Course body) {
        if (body.getCourseId() < 1) throw new InvalidInputException("Invalid courseId: " + body.getCourseId());

        CourseEntity entity = mapper.apiToEntity(body);
        Mono<Course> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    /**
     *
     *
     * @param courseId
     * @param delay - number of seconds; causes the service to delay the response
     * @param faultPercent - probability; causes the service to throw an exc
     * @return
     */
    @Override
    public Mono<Course> getCourse(int courseId, int delay, int faultPercent) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        if (delay > 0) simulateDelay(delay);
        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        return repository.findByCourseId(courseId)
                .switchIfEmpty(error(new NotFoundException("No course found for courseId: " + courseId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteCourse(int courseId) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.debug("deleteCourse: tries to delete an entity with courseId: {}", courseId);
        repository.findByCourseId(courseId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();

    }

    private void simulateDelay(int delay) {
        LOG.debug("Sleeping for {} seconds...", delay);
        try {Thread.sleep(delay * 1000);} catch (InterruptedException e) {}
        LOG.debug("Moving on...");
    }


    /**
     *
     * @param faultPercent - fault percentage
     * Creates a random number between 1 and 100 and throws exc if its >= the specified fault percentage
     *
     */
    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }

}
