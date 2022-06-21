package com.distributed.systems.userservice;

import org.junit.jupiter.api.Test;
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
class UserServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	@Test
	void getUserById() {
		int userId = 1;

		client.get()
				.uri("/user/" + userId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.userId").isEqualTo(userId);
	}

	@Test
	public void getUserInvalidParameterString() {

		client.get()
				.uri("/user/no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/user/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getUserNotFound() {

		int userIdNotFound = 13;

		client.get()
				.uri("/user/" + userIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/user/" + userIdNotFound)
				.jsonPath("$.message").isEqualTo("No user with id: " + userIdNotFound);
	}

	@Test
	public void getUserInvalidParameterNegativeValue() {

		int userIdInvalid = -1;

		client.get()
				.uri("/user/" + userIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/user/" + userIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid userId: " + userIdInvalid);
	}

}
