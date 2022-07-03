package com.distributed.systems.api.core.user;

import com.distributed.systems.api.core.course.Course;
import org.springframework.web.bind.annotation.*;

public interface UserService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/user \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/user",
            consumes = "application/json",
            produces = "application/json")
    User createUser(@RequestBody User body);

    /**
     *
     * @param userId
     * @return
     */
    @GetMapping(
            value = "/user/{userId}",
            produces = "application/json"
    )
    public User getUser(@PathVariable int userId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/user/{userId}
     *
     * @param userId
     */
    @DeleteMapping(
            value    = "/user/{userId}",
            produces = "application/json")
    void deleteUser(@PathVariable int userId);
}
