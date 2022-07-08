package com.distributed.systems.ratingservice;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.ratingservice.persistence.RatingRepository;
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
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"logging.level.se.magnus=DEBUG", "eureka.client.enabled=false", "spring.datasource.url=jdbc:h2:mem:rating-db"})
public class RatingServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private RatingRepository repository;
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getRatingsByCourseId() {
		int courseId = 1;

		assertEquals(0, repository.findByCourseId(courseId).size());

		sendCreateRatingEvent(courseId, 1);
		sendCreateRatingEvent(courseId, 2);
		sendCreateRatingEvent(courseId, 3);

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

	@Test
	public void deleteRatings() {

		int courseId = 1;
		int ratingId = 1;

		sendCreateRatingEvent(courseId, ratingId);
		assertEquals(1, repository.findByCourseId(courseId).size());

		sendDeleteRatingEvent(courseId);
		assertEquals(0, repository.findByCourseId(courseId).size());

		sendDeleteRatingEvent(courseId);
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

	private void sendCreateRatingEvent(int courseId, int ratingId) {
		Rating rating = new Rating(ratingId, courseId,  "text", 1, 5, null);
		Event<Integer, Rating> event = new Event(CREATE, courseId, rating);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteRatingEvent(int courseId) {
		Event<Integer, Rating> event = new Event(DELETE, courseId, null);
		input.send(new GenericMessage<>(event));
	}
}
