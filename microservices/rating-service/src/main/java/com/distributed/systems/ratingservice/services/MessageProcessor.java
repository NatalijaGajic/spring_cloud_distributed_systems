package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
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

    private final RatingService ratingService;

    @Autowired
    public MessageProcessor(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Rating> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Rating rating = event.getData();
                LOG.info("Create rating with ID: {}/{}", rating.getCourseId(), rating.getRatingId());
                ratingService.createRating(rating);
                break;

            case DELETE:
                int courseId = event.getKey();
                LOG.info("Delete ratings with CourseID: {}", courseId);
                ratingService.deleteRatings(courseId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
