package com.distributed.systems.api.core.lecture;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LectureService {


    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/lecture \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/lecture",
            consumes = "application/json",
            produces = "application/json")
    Lecture createLecture(@RequestBody Lecture body);

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/lecture",
            produces = "application/json"
    )
    public Flux<Lecture> getLectures(@RequestParam(value = "courseId", required = true) int courseId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/lecture?courseId=1
     *
     * @param courseId
     */
    @DeleteMapping(value = "/lecture")
    void deleteLectures(@RequestParam(value = "courseId", required = true)  int courseId);
}
