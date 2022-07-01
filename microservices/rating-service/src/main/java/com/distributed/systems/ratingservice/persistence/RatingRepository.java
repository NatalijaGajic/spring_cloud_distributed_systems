package com.distributed.systems.ratingservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface RatingRepository extends CrudRepository<RatingEntity, Integer> {

    @Transactional(readOnly = true)
    List<RatingEntity> findByCourseId(int courseId);
}
