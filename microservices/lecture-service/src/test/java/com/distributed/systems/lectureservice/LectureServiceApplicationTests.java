package com.distributed.systems.lectureservice;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.lectureservice.persistence.LectureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.distributed.systems.api.event.Event.Type.CREATE;
import static com.distributed.systems.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class LectureServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private LectureRepository repository;


	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getLecturesByCourseId() {
		int courseId = 1;

		sendCreateLectureEvent(courseId, 1);
		sendCreateLectureEvent(courseId, 2);
		sendCreateLectureEvent(courseId, 3);

		assertEquals(3, (long)repository.findByCourseId(courseId).count().block());

		getAndVerifyLecturesByCourseId(courseId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].courseId").isEqualTo(courseId)
				.jsonPath("$[2].lectureId").isEqualTo(3);
	}

	/*@Test
	public void duplicateError() {

		int courseId = 1;
		int lectureId = 1;

		postAndVerifyLecture(courseId, lectureId, OK)
				.jsonPath("$.courseId").isEqualTo(courseId)
				.jsonPath("$.lectureId").isEqualTo(lectureId);

		assertEquals(1, repository.count());

		postAndVerifyLecture(courseId, lectureId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Duplicate key, Course Id: 1, Lecture Id:1");

		assertEquals(1, repository.count());
	}*/

	@Test
	public void deleteLectures() {

		int courseId = 1;
		int lectureId = 1;

		sendCreateLectureEvent(courseId, lectureId);
		assertEquals(1, (long)repository.findByCourseId(courseId).count().block());


		sendDeleteLectureEvent(courseId);
		assertEquals(0, (long)repository.findByCourseId(courseId).count().block());


		sendDeleteLectureEvent(courseId);
	}


	@Test
	public void getLecturesMissingParameter() {

		getAndVerifyLecturesByCourseId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Required int parameter 'courseId' is not present");

	}

	@Test
	public void getLecturesInvalidParameter() {

		getAndVerifyLecturesByCourseId("?courseId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getLecturesNotFound() {

		int courseIdNotFound = 123;

		getAndVerifyLecturesByCourseId("?courseId="+courseIdNotFound, OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getLecturesInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		getAndVerifyLecturesByCourseId("?courseId=" + courseIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyLecturesByCourseId(int courseId, HttpStatus expectedStatus) {
		return getAndVerifyLecturesByCourseId("?courseId=" + courseId, expectedStatus);
	}
	private WebTestClient.BodyContentSpec getAndVerifyLecturesByCourseId(String courseIdQuery, HttpStatus expectedStatus){
		return client.get()
				.uri("/lecture" + courseIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void sendCreateLectureEvent(int courseId, int lectureId) {
		Lecture lecture = new Lecture(lectureId, courseId, "text", "details", 5, 14);
		Event<Integer, Lecture> event = new Event(CREATE, courseId, lecture);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteLectureEvent(int courseId) {
		Event<Integer, Lecture> event = new Event(DELETE, courseId, null);
		input.send(new GenericMessage<>(event));
	}

}
