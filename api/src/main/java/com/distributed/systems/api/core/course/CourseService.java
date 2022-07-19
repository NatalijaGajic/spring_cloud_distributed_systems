package com.distributed.systems.api.core.course;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
     * @param delay - number of seconds; causes the service to delay the response
     * @param faultPercent - probability; causes the service to throw an exc
     * @return the course, if found, else null
     */
    @GetMapping(
            value = "/course/{courseId}",
            produces = "application/json"
    )
    Mono<Course> getCourse(
            @PathVariable int courseId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);

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
