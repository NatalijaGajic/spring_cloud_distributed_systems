package com.distributed.systems.lectureservice;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class LectureServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getLecturesByCourseId() {
		int courseId = 1;

		client.get()
				.uri("/lecture?courseId=" + courseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].courseId").isEqualTo(courseId);
	}

	@Test
	public void getLecturesMissingParameter() {

		client.get()
				.uri("/lecture")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Required request parameter 'courseId' for method parameter type int is not present");
	}

	@Test
	public void getLecturesInvalidParameter() {

		client.get()
				.uri("/lecture?courseId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getLecturesNotFound() {

		int courseIdNotFound = 123;

		client.get()
				.uri("/lecture?courseId=" + courseIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getLecturesInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		client.get()
				.uri("/lecture?courseId=" + courseIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}
}
