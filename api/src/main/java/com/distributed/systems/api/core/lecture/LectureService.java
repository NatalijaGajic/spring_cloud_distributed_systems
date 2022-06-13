package com.distributed.systems.api.core.lecture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;
import java.util.List;

public interface LectureService {

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/lecture",
            produces = "application/json"
    )
    public List<Lecture> getLectures(@RequestParam(value = "courseId", required = true) int courseId);
}
