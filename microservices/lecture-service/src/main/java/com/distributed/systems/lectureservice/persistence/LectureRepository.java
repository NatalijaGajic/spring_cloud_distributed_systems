package com.distributed.systems.lectureservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LectureRepository extends ReactiveCrudRepository<LectureEntity, String> {
    Flux<LectureEntity> findByCourseId(int courseId);
}
