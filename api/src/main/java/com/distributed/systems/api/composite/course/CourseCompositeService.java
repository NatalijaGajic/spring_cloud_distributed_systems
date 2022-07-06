package com.distributed.systems.api.composite.course;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Api(description = "REST API for compose course information")
public interface CourseCompositeService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/product-composite \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","weight":123}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.course-composite.create-composite-course.description}",
            notes = "${api.course-composite.create-composite-course.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/course-composite",
            consumes = "application/json")
    void createCompositeCourse(@RequestBody CourseAggregate body);


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
    public Mono<CourseAggregate> getCourse(@PathVariable int courseId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/course-composite/1
     *
     * @param courseId
     */
    @ApiOperation(
            value = "${api.course-composite.delete-composite-course.description}",
            notes = "${api.course-composite.delete-composite-course.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/course-composite/{courseId}")
    void deleteCompositeCourse(@PathVariable int courseId);
}
