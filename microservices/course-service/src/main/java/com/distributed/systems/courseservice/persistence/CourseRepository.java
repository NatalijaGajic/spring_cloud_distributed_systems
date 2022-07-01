package com.distributed.systems.courseservice.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CourseRepository extends PagingAndSortingRepository<CourseEntity, String> {
    Optional<CourseEntity> findByCourseId(int courseId);
}
