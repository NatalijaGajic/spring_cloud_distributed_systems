package com.distributed.systems.api.composite.course;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Api(description = "REST API for compose course information")
public interface CourseCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/course-composite/1
     *
     * @param courseId
     * @return
     */
    @ApiOperation(
            value = "${api.course-composite.get-composite-course.description}",
            notes = "${api.course-composite.get-composite-course.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    @GetMapping(
            value ="/course-composite/{courseId}",
            produces = "application/json"
    )
    public CourseAggregate getCourse(@PathVariable int courseId);
}
