package com.distributed.systems.courseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
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
    private final CourseService courseService;

    @Autowired
    public MessageProcessor(CourseService courseService) {
        this.courseService = courseService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Course> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Course course = event.getData();
                LOG.info("Create course with ID: {}", course.getCourseId());
                courseService.createCourse(course);
                break;

            case DELETE:
                int courseId = event.getKey();
                LOG.info("Delete course with CourseID: {}", courseId);
                courseService.deleteCourse(courseId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
