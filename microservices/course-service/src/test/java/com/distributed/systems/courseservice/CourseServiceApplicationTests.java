package com.distributed.systems.courseservice;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.courseservice.persistence.CourseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class CourseServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private CourseRepository repository;

	@Before
	public void setupDb() {

		repository.deleteAll().block();
	}

	@Test
	public void getCourseById() {
		int courseId = 1;

		postAndVerifyCourse(courseId, OK);
		assertNotNull(repository.findByCourseId(courseId).block());

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

		postAndVerifyCourse(courseId, OK);
		assertNotNull(repository.findByCourseId(courseId).block());

		deleteAndVerifyCourse(courseId, OK);
		assertNull(repository.findByCourseId(courseId).block());

		deleteAndVerifyCourse(courseId, OK);
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
	private WebTestClient.BodyContentSpec postAndVerifyCourse(int courseId, HttpStatus expectedStatus) {
		Course course = new Course(courseId, "Data Science with Python", 5, "$");
		return client.post()
				.uri("/course")
				.body(just(course), Course.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}


	private WebTestClient.BodyContentSpec deleteAndVerifyCourse(int courseId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/course/" + courseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
