package com.distributed.systems.api.composite.course;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CourseCompositeService {

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value ="/course-composite/{courseId}",
            produces = "application/json"
    )
    public CourseAggregate getCourse(@PathVariable int courseId);
}
