package com.distributed.systems.purchaseservice;

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
class PurchaseServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	@Test
	void getPurchaseById() {
		int purchaseId = 1;

		client.get()
				.uri("/purchase/" + purchaseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.purchaseId").isEqualTo(purchaseId);
	}

	@Test
	public void getPurchaseInvalidParameterString() {

		client.get()
				.uri("/purchase/no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/purchase/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getPurchaseNotFound() {

		int purchaseIdNotFound = 13;

		client.get()
				.uri("/purchase/" + purchaseIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/purchase/" + purchaseIdNotFound)
				.jsonPath("$.message").isEqualTo("No purchase with id: " + purchaseIdNotFound);
	}

	@Test
	public void getPurchaseInvalidParameterNegativeValue() {

		int purchaseIdInvalid = -1;

		client.get()
				.uri("/purchase/" + purchaseIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/purchase/" + purchaseIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid purchaseId: " + purchaseIdInvalid);
	}

}
