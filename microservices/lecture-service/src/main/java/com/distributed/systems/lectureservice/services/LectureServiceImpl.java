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
        try {
            LectureEntity entity = mapper.apiToEntity(body);
            LectureEntity newEntity = repository.save(entity);

            LOG.debug("createLecture: created a lecture entity: {}/{}", body.getCourseId(), body.getLectureId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId() + ", Lecture Id:" + body.getLectureId());
        }
    }

    @Override
    public List<Lecture> getLectures(int courseId) {
        if(courseId < 1) throw new InvalidInputException("Invalid courseId: "+courseId);

        List<LectureEntity> entityList = repository.findByCourseId(courseId);
        List<Lecture> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("/lectures response size: {}", list.size());
        return list;
    }

    @Override
    public void deleteLectures(int courseId) {
        LOG.debug("deleteLecture: tries to delete lecture for the course with courseId: {}", courseId);
        repository.deleteAll(repository.findByCourseId(courseId));
    }
}
