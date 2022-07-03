package com.distributed.systems.api.core.course;

import org.springframework.web.bind.annotation.*;

public interface CourseService {


    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/course \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/course",
            consumes = "application/json",
            produces = "application/json")
    Course createCourse(@RequestBody Course body);

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

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/course/1
     *
     * @param courseId
     */
    @DeleteMapping(value = "/course/{courseId}")
    void deleteCourse(@PathVariable int courseId);
}
