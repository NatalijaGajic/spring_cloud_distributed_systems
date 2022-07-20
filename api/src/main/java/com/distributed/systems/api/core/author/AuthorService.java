package com.distributed.systems.api.core.author;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

public interface AuthorService {
    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/author \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/author",
            consumes = "application/json",
            produces = "application/json")
    Author createAuthor(@RequestBody Author body);

    /**
     *
     * @param courseId
     * @return
     */
    @GetMapping(
            value = "/author",
            produces = "application/json"
    )
    public Flux<Author> getAuthors(@RequestParam(value = "courseId", required = true) int courseId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/author?courseId=1
     *
     * @param courseId
     */
    @DeleteMapping(value = "/author")
    void deleteAuthors(@RequestParam(value = "courseId", required = true)  int courseId);
}
