package com.distributed.systems.courseservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CourseServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getCourseById() {
		int courseId = 1;

		client.get()
				.uri("/course/" + courseId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.courseId").isEqualTo(courseId);
	}

	@Test
	public void getCourseInvalidParameterString() {

		client.get()
				.uri("/course/no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/course/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getCourseNotFound() {

		int courseIdNotFound = 13;

		client.get()
				.uri("/course/" + courseIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/course/" + courseIdNotFound)
				.jsonPath("$.message").isEqualTo("No course with id: " + courseIdNotFound);
	}

	@Test
	public void getCourseInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		client.get()
				.uri("/course/" + courseIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/course/" + courseIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}


}
