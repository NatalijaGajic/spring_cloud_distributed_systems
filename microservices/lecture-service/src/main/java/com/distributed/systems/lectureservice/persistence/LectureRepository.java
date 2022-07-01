package com.distributed.systems.lectureservice.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LectureRepository extends CrudRepository<LectureEntity, String> {
    List<LectureEntity> findByCourseId(int courseId);
}
