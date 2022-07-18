package com.distributed.systems.userservice;

import com.distributed.systems.api.core.user.User;
import com.distributed.systems.userservice.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false", "spring.cloud.config.enabled=false", "server.error.include-message=always"})
public class UserServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	@Autowired
	private UserRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getUserById() {
		int userId = 1;


		postAndVerifyUser(userId, OK);

		assertTrue(repository.findByUserId(userId).isPresent());

		getAndVerifyUser(userId, OK)
				.jsonPath("$.userId").isEqualTo(userId);
	}

	@Test
	public void getUserInvalidParameterString() {

		getAndVerifyUser("/no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/user/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getUserNotFound() {

		int userIdNotFound = 13;

		getAndVerifyUser(userIdNotFound, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/user/" + userIdNotFound)
				.jsonPath("$.message").isEqualTo("No user found for userId: " + userIdNotFound);
	}

	@Test
	public void getUserInvalidParameterNegativeValue() {

		int userIdInvalid = -1;

		getAndVerifyUser(userIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/user/" + userIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid userId: " + userIdInvalid);
	}


	private WebTestClient.BodyContentSpec getAndVerifyUser(int userId, HttpStatus expectedStatus) {
		return getAndVerifyUser("/" + userId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyUser(String userIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/user" + userIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyUser(int userId, HttpStatus expectedStatus) {
		User user = new User(userId, "Natalija Gajic", null, null, "SA");
		return client.post()
				.uri("/user")
				.body(just(user), User.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/product/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
