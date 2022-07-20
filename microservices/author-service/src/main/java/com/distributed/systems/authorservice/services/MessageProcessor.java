package com.distributed.systems.authorservice.services;

import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.api.core.author.AuthorService;
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
    private final AuthorService authorService;

    @Autowired
    public MessageProcessor(AuthorService authorService) {
        this.authorService = authorService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Author> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Author author = event.getData();
                LOG.info("Create author with ID: {}/{}", author.getCourseId(), author.getAuthorId());
                authorService.createAuthor(author);
                break;

            case DELETE:
                int courseId = event.getKey();
                LOG.info("Delete author with CourseID: {}", courseId);
                authorService.deleteAuthors(courseId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
