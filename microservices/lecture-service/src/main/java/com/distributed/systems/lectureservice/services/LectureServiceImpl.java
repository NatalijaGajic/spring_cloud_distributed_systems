package com.distributed.systems.lectureservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LectureServiceImpl implements LectureService {
    @Override
    public List<Lecture> getLectures(int courseId) {
        return null;
    }
}
