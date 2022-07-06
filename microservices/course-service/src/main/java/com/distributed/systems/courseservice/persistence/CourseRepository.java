package com.distributed.systems.courseservice.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseRepository extends ReactiveCrudRepository<CourseEntity, String> {
    Mono<CourseEntity> findByCourseId(int courseId);
}
