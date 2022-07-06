package com.distributed.systems.lectureservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);
    private final LectureService lectureService;

    @Autowired
    public MessageProcessor(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Lecture> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Lecture lecture = event.getData();
                LOG.info("Create lecture with ID: {}/{}", lecture.getCourseId(), lecture.getLectureId());
                lectureService.createLecture(lecture);
                break;

            case DELETE:
                int courseId = event.getKey();
                LOG.info("Delete lecture with CourseID: {}", courseId);
                lectureService.deleteLectures(courseId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
