package com.distributed.systems.lectureservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LectureServiceImpl implements LectureService {

    private static final Logger LOG = LoggerFactory.getLogger(LectureServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public LectureServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Lecture> getLectures(int courseId) {
        if(courseId < 1) throw new InvalidInputException("Invalid courseId: "+courseId);
        if(courseId == 123){
            LOG.debug("No lectures found for courseId: {}", courseId);
            return new ArrayList<>();
        }
        List<Lecture> list = new ArrayList<>();
        list.add(new Lecture(1, courseId, "Lecture 1", 7, serviceUtil.getServiceAddress()));
        list.add(new Lecture(2, courseId, "Lecture 2", 13, serviceUtil.getServiceAddress()));
        list.add(new Lecture(3, courseId, "Lecture 3", 8, serviceUtil.getServiceAddress()));

        LOG.debug("/lectures response size: {}", list.size());
        return list;
    }
}
