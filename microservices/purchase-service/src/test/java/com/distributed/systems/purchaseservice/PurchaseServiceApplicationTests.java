package com.distributed.systems.purchaseservice;

import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.purchaseservice.persistence.PurchaseRepository;
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
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class PurchaseServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private PurchaseRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}
	@Test
	public void getPurchaseById() {
		int purchaseId = 1;

		postAndVerifyPurchase(purchaseId, OK);

		assertTrue(repository.findByPurchaseId(purchaseId).isPresent());

		getAndVerifyPurchase(purchaseId, OK)
				.jsonPath("$.purchaseId").isEqualTo(purchaseId);
	}

	@Test
	public void getPurchaseInvalidParameterString() {

		getAndVerifyPurchase("/no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/purchase/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getPurchaseNotFound() {

		int purchaseIdNotFound = 13;

		getAndVerifyPurchase(purchaseIdNotFound, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/purchase/" + purchaseIdNotFound)
				.jsonPath("$.message").isEqualTo("No purchase found for purchaseId: " + purchaseIdNotFound);

	}

	@Test
	public void getPurchaseInvalidParameterNegativeValue() {

		int purchaseIdInvalid = -1;

		getAndVerifyPurchase(purchaseIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/purchase/" + purchaseIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid purchaseId: " + purchaseIdInvalid);
	}

	private WebTestClient.BodyContentSpec postAndVerifyPurchase(int purchaseId, HttpStatus expectedStatus){
		Purchase product = new Purchase(purchaseId, 2, 3, null, 8, "$", "SA");
		return client.post()
				.uri("/purchase")
				.body(just(product), Purchase.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	//TODO:duplicateError
	@Test
	public void deletePurchase() {

		int purchaseId = 1;

		postAndVerifyPurchase(purchaseId, OK);
		assertTrue(repository.findByPurchaseId(purchaseId).isPresent());

		deleteAndVerifyPurchase(purchaseId, OK);
		assertFalse(repository.findByPurchaseId(purchaseId).isPresent());

		deleteAndVerifyPurchase(purchaseId, OK);
	}
	private WebTestClient.BodyContentSpec getAndVerifyPurchase(int purchaseId, HttpStatus expectedStatus) {
		return getAndVerifyPurchase("/" + purchaseId, expectedStatus);
	}
	private WebTestClient.BodyContentSpec getAndVerifyPurchase(String purchaseIdPath, HttpStatus expectedStatus){
		return client.get()
				.uri("/purchase" + purchaseIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyPurchase(int purchaseId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/purchase/" + purchaseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
