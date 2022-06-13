package com.distributed.systems.api.core.rating;

import com.distributed.systems.api.core.lecture.Lecture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RatingService {

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/rating",
            produces = "application/json"
    )
    public List<Rating> getRating(@RequestParam(value = "courseId", required = true) int courseId);
}
