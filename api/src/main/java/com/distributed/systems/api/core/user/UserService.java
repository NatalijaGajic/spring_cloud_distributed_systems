package com.distributed.systems.api.core.user;

import com.distributed.systems.api.core.course.Course;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserService {

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
}
