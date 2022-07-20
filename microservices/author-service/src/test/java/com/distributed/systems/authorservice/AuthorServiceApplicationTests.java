package com.distributed.systems.authorservice;


import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.authorservice.persistence.AuthorRepository;
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
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false", "spring.cloud.config.enabled=false", "server.error.include-message=always"})
public class AuthorServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private AuthorRepository repository;


	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getAuthorsByCourseId() {
		int courseId = 1;

		sendCreateAuthorEvent(courseId, 1);
        sendCreateAuthorEvent(courseId, 2);
        sendCreateAuthorEvent(courseId, 3);

		assertEquals(3, (long)repository.findByCourseId(courseId).count().block());

        getAndVerifyAuthorsByCourseId(courseId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].courseId").isEqualTo(courseId)
				.jsonPath("$[2].authorId").isEqualTo(3);
	}



	@Test
	public void deleteAuthors() {

		int courseId = 1;
		int lectureId = 1;

		sendCreateAuthorEvent(courseId, lectureId);
		assertEquals(1, (long)repository.findByCourseId(courseId).count().block());


        sendDeleteAuthorEvent(courseId);
		assertEquals(0, (long)repository.findByCourseId(courseId).count().block());


        sendDeleteAuthorEvent(courseId);
	}


	@Test
	public void getAuthorsMissingParameter() {

        getAndVerifyAuthorsByCourseId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/author")
				.jsonPath("$.message").isEqualTo("Required int parameter 'courseId' is not present");

	}

	@Test
	public void getAuthorsInvalidParameter() {

        getAndVerifyAuthorsByCourseId("?courseId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/author")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getAuthorsNotFound() {

		int courseIdNotFound = 123;

        getAndVerifyAuthorsByCourseId("?courseId="+courseIdNotFound, OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getAuthorsInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

        getAndVerifyAuthorsByCourseId("?courseId=" + courseIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/author")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyAuthorsByCourseId(int courseId, HttpStatus expectedStatus) {
		return getAndVerifyAuthorsByCourseId("?courseId=" + courseId, expectedStatus);
	}
	private WebTestClient.BodyContentSpec getAndVerifyAuthorsByCourseId(String courseIdQuery, HttpStatus expectedStatus){
		return client.get()
				.uri("/author" + courseIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void sendCreateAuthorEvent(int courseId, int authorId) {
		Author author = new Author(authorId, courseId, "text", "country", 1, null);
		Event<Integer, Author> event = new Event(CREATE, courseId, author);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteAuthorEvent(int courseId) {
		Event<Integer, Author> event = new Event(DELETE, courseId, null);
		input.send(new GenericMessage<>(event));
	}

}
