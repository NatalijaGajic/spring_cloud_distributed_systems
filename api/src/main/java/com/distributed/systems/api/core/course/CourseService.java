package com.distributed.systems.api.core.course;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CourseService {

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/course/{courseId}",
            produces = "application/json"
    )
    public Course getCourse(@PathVariable int courseId);
}
