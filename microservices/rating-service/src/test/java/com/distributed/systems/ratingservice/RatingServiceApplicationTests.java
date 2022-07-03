package com.distributed.systems.ratingservice;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.ratingservice.persistence.RatingRepository;
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
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})public class RatingServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private RatingRepository repository;


	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getRatingsByCourseId() {
		int courseId = 1;

		assertEquals(0, repository.findByCourseId(courseId).size());

		postAndVerifyRating(courseId, 1, OK);
		postAndVerifyRating(courseId, 2, OK);
		postAndVerifyRating(courseId, 3, OK);

		assertEquals(3, repository.findByCourseId(courseId).size());

		getAndVerifyRatingsByCourseId(courseId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].courseId").isEqualTo(courseId)
				.jsonPath("$[2].ratingId").isEqualTo(3);
	}

	@Test
	public void getRatingsMissingParameter() {

		getAndVerifyRatingsByCourseId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Required int parameter 'courseId' is not present");
	}

	@Test
	public void getRatingsInvalidParameter() {

		getAndVerifyRatingsByCourseId("?courseId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getRatingsNotFound() {

		getAndVerifyRatingsByCourseId("?courseId=213", OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getRatingsInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		getAndVerifyRatingsByCourseId("?courseId=" + courseIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyRatingsByCourseId(int courseId, HttpStatus expectedStatus) {
		return getAndVerifyRatingsByCourseId("?courseId=" + courseId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyRatingsByCourseId(String courseIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/rating" + courseIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyRating(int courseId, int ratingId, HttpStatus expectedStatus) {
		Rating rating = new Rating(ratingId, courseId, 5, 4, "Good", "SA");
		return client.post()
				.uri("/rating")
				.body(just(rating), Rating.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/review?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
}
