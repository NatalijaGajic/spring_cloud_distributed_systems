package com.distributed.systems.ratingservice;

import org.junit.Test;
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
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class RatingServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	@Test
	public void getRatingsByCourseId() {
		int courseId = 1;

		client.get()
				.uri("/rating?courseId=" + courseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].courseId").isEqualTo(courseId);
	}

	@Test
	public void getRatingsMissingParameter() {

		client.get()
				.uri("/rating")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Required int parameter 'courseId' is not present");
	}

	@Test
	public void getRatingsInvalidParameter() {

		client.get()
				.uri("/rating?courseId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getRatingsNotFound() {

		int courseIdNotFound = 123;

		client.get()
				.uri("/rating?courseId=" + courseIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getRatingsInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		client.get()
				.uri("/rating?courseId=" + courseIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}
}
