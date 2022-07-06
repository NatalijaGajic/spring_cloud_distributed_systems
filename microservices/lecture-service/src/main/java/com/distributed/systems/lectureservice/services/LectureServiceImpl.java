package com.distributed.systems.lectureservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import com.distributed.systems.lectureservice.persistence.LectureEntity;
import com.distributed.systems.lectureservice.persistence.LectureRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LectureServiceImpl implements LectureService {

    private static final Logger LOG = LoggerFactory.getLogger(LectureServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final LectureMapper mapper;
    private final LectureRepository repository;

    @Autowired
    public LectureServiceImpl(ServiceUtil serviceUtil, LectureMapper mapper, LectureRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Lecture createLecture(Lecture body) {
        if (body.getCourseId() < 1) throw new InvalidInputException("Invalid courseId: " + body.getCourseId());

        LectureEntity entity = mapper.apiToEntity(body);
        Mono<Lecture> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId() + ", Lecture Id:" + body.getLectureId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();

    }

    @Override
    public Flux<Lecture> getLectures(int courseId) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        return repository.findByCourseId(courseId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteLectures(int courseId) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.debug("deleteLectures: tries to delete lectures for the product with courseId: {}", courseId);
        repository.deleteAll(repository.findByCourseId(courseId)).block();

    }
}
