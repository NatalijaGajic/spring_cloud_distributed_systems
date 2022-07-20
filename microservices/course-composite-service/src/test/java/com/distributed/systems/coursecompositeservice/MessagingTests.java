package com.distributed.systems.coursecompositeservice;

import com.distributed.systems.api.composite.course.AuthorSummary;
import com.distributed.systems.api.composite.course.CourseAggregate;
import com.distributed.systems.api.composite.course.LectureSummary;
import com.distributed.systems.api.composite.course.RatingSummary;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.coursecompositeservice.services.CourseCompositeIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.BlockingQueue;

import static com.distributed.systems.api.event.Event.Type.CREATE;
import static com.distributed.systems.api.event.Event.Type.DELETE;
import static com.distributed.systems.coursecompositeservice.IsSameEvent.sameEventExceptCreatedAt;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment=RANDOM_PORT,
        classes = {CourseCompositeServiceApplication.class, TestSecurityConfig.class },
        properties = {"spring.main.allow-bean-definition-overriding=true","eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
public class MessagingTests {

    private static final int COURSE_ID_OK = 1;
    private static final int COURSE_ID_NOT_FOUND = 2;
    private static final int COURSE_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @Autowired
    private CourseCompositeIntegration.MessageSources channels;

    @Autowired
    private MessageCollector collector;

    BlockingQueue<Message<?>> queueCourses = null;
    BlockingQueue<Message<?>> queueLectures = null;
    BlockingQueue<Message<?>> queueRatings = null;

    @Before
    public void setUp() {
        queueCourses = getQueue(channels.outputCourses());
        queueLectures = getQueue(channels.outputLectures());
        queueRatings = getQueue(channels.outputRatings());
    }

    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    @Test
    public void createCompositeCourse1() {

        CourseAggregate composite = new CourseAggregate(1, "title", "details", "eng");
        postAndVerifyCourse(composite, OK);

        // Assert one expected new product events queued up
        assertEquals(1, queueCourses.size());

        Event<Integer, Course> expectedEvent = new Event(CREATE, composite.getCourseId(), new Course(composite.getCourseId(), composite.getCourseTitle(), composite.getCourseDetails(), composite.getLanguage()));
        assertThat(queueCourses, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert none recommendations and review events
        assertEquals(0, queueLectures.size());
        assertEquals(0, queueRatings.size());
    }

    @Test
    public void createCompositeCourse2() {

        CourseAggregate composite = new CourseAggregate(1, "title", "details", "eng",
                singletonList(new RatingSummary(1, 1, 2, 3, "text", null)),
                singletonList(new LectureSummary(1, 1, "title", "details", 1, 7)),
                singletonList(new AuthorSummary(1, "fullName", "country", 1)));

        postAndVerifyCourse(composite, OK);

        // Assert one create product event queued up
        assertEquals(1, queueCourses.size());

        Event<Integer, Course> expectedCourseEvent = new Event(CREATE, composite.getCourseId(), new Course(composite.getCourseId(), composite.getCourseTitle(), composite.getCourseDetails(), composite.getLanguage()));
        assertThat(queueCourses, receivesPayloadThat(sameEventExceptCreatedAt(expectedCourseEvent)));

        // Assert one create recommendation event queued up
        assertEquals(1, queueLectures.size());

        LectureSummary lec = composite.getLectures().get(0);
        Event<Integer, Course> expectedLectureEvent = new Event(CREATE, composite.getCourseId(), new Lecture(lec.getLectureId(), composite.getCourseId(), lec.getLectureTitle(), lec.getLectureDetails(), lec.getLectureOrder(), lec.getDurationInMinutes()));
        assertThat(queueLectures, receivesPayloadThat(sameEventExceptCreatedAt(expectedLectureEvent)));

        // Assert one create review event queued up
        assertEquals(1, queueRatings.size());

        RatingSummary rat = composite.getRatings().get(0);
        Event<Integer, Course> expectedRatingEvent = new Event(CREATE, composite.getCourseId(), new Rating(rat.getRatingId(), rat.getCourseId(),  rat.getText(), rat.getUserId(), rat.getStarRating(), rat.getRatingCreatedDate()));
        assertThat(queueRatings, receivesPayloadThat(sameEventExceptCreatedAt(expectedRatingEvent)));
    }

    @Test
    public void deleteCompositeProduct() {

        deleteAndVerifyCourse(1, OK);

        // Assert one delete product event queued up
        assertEquals(1, queueCourses.size());

        Event<Integer, Course> expectedEvent = new Event(DELETE, 1, null);
        assertThat(queueCourses, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert one delete recommendation event queued up
        assertEquals(1, queueLectures.size());

        Event<Integer, Course> expectedLectureEvent = new Event(DELETE, 1, null);
        assertThat(queueLectures, receivesPayloadThat(sameEventExceptCreatedAt(expectedLectureEvent)));

        // Assert one delete review event queued up
        assertEquals(1, queueRatings.size());

        Event<Integer, Course> expectedRatingEvent = new Event(DELETE, 1, null);
        assertThat(queueRatings, receivesPayloadThat(sameEventExceptCreatedAt(expectedRatingEvent)));
    }


    private void postAndVerifyCourse(CourseAggregate compositeProduct, HttpStatus expectedStatus) {
        client.post()
                .uri("/course-composite")
                .body(just(compositeProduct), CourseAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyCourse(int courseId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/course-composite/" + courseId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

}
