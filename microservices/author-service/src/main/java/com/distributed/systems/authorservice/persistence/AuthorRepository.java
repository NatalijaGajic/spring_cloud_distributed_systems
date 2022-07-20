package com.distributed.systems.authorservice.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AuthorRepository extends ReactiveCrudRepository<AuthorEntity, String> {
   Flux<AuthorEntity> findByCourseId(int courseId);
}
