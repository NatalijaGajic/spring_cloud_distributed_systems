package com.distributed.systems.api.core.rating;

import com.distributed.systems.api.core.lecture.Lecture;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RatingService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/rating \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/rating",
            consumes = "application/json",
            produces = "application/json")
    Rating createRating(@RequestBody Rating body);
    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/rating",
            produces = "application/json"
    )
    public List<Rating> getRatings(@RequestParam(value = "courseId", required = true) int courseId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/rating?courseId=1
     *
     * @param courseId
     */
    @DeleteMapping(value = "/rating")
    void deleteRatings(@RequestParam(value = "courseId", required = true)  int courseId);
}
