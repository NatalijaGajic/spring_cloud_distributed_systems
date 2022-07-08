package com.distributed.systems.courseservice;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.courseservice.persistence.CourseRepository;
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
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class CourseServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private CourseRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getCourseById() {
		int courseId = 1;

		assertNull(repository.findByCourseId(courseId).block());
		assertEquals(0, (long)repository.count().block());

		sendCreateCourseEvent(courseId);

		assertNotNull(repository.findByCourseId(courseId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyCourse(String.valueOf(courseId), OK)
				.jsonPath("$.courseId").isEqualTo(courseId);
	}

	/*@Test
	public void duplicateError() {

		int courseId = 1;

		postAndVerifyCourse(courseId, OK);

		assertTrue(repository.findByCourseId(courseId).isPresent());

		postAndVerifyCourse(courseId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/course")
				.jsonPath("$.message").isEqualTo("Duplicate key, Course Id: " + courseId);
	}*/

	@Test
	public void deleteCourse() {

		int courseId = 1;

		sendCreateCourseEvent(courseId);

		assertNotNull(repository.findByCourseId(courseId).block());

		sendDeleteCourseEvent(courseId);
		assertNull(repository.findByCourseId(courseId).block());

		sendCreateCourseEvent(courseId);
	}


	@Test
	public void getCourseInvalidParameterString() {

		getAndVerifyCourse("/no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/course/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getCourseNotFound() {

		int courseIdNotFound = 13;

		getAndVerifyCourse(String.valueOf(courseIdNotFound), NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/course/" + courseIdNotFound)
				.jsonPath("$.message").isEqualTo("No course found for courseId: " + courseIdNotFound);
	}


	@Test
	public void getCourseInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		getAndVerifyCourse(String.valueOf(courseIdInvalid), UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/course/" + courseIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);

	}

	private WebTestClient.BodyContentSpec getAndVerifyCourse(String courseIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/course/" + courseIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void sendCreateCourseEvent(int courseId) {
		Course course = new Course(courseId, "title", "details", "eng");
		Event<Integer, Course> event = new Event(CREATE, courseId, course);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteCourseEvent(int courseId) {
		Event<Integer, Course> event = new Event(DELETE, courseId, null);
		input.send(new GenericMessage<>(event));
	}


}
