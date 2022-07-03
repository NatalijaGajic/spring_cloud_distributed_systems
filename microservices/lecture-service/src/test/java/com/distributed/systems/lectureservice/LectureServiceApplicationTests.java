package com.distributed.systems.lectureservice;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.lectureservice.persistence.LectureRepository;
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
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class LectureServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private LectureRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getLecturesByCourseId() {
		int courseId = 1;

		postAndVerifyLecture(courseId, 1, OK);
		postAndVerifyLecture(courseId, 2, OK);
		postAndVerifyLecture(courseId, 3, OK);

		assertEquals(3, repository.findByCourseId(courseId).size());

		getAndVerifyLecturesByCourseId(courseId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].courseId").isEqualTo(courseId)
				.jsonPath("$[2].lectureId").isEqualTo(1);
	}

	/*@Test
	public void duplicateError() {

		int courseId = 1;
		int lectureId = 1;

		postAndVerifyLecture(courseId, lectureId, OK)
				.jsonPath("$.courseId").isEqualTo(courseId)
				.jsonPath("$.lectureId").isEqualTo(lectureId);

		assertEquals(1, repository.count());

		postAndVerifyLecture(courseId, lectureId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Duplicate key, Course Id: 1, Lecture Id:1");

		assertEquals(1, repository.count());
	}*/

	@Test
	public void deleteLectures() {

		int courseId = 1;
		int lectureId = 1;

		postAndVerifyLecture(courseId, lectureId, OK);
		assertEquals(1, repository.findByCourseId(courseId).size());

		deleteAndVerifyLecturesByCourseId(courseId, OK);
		assertEquals(0, repository.findByCourseId(courseId).size());

		deleteAndVerifyLecturesByCourseId(courseId, OK);
	}


	@Test
	public void getLecturesMissingParameter() {

		getAndVerifyLecturesByCourseId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Required int parameter 'courseId' is not present");

	}

	@Test
	public void getLecturesInvalidParameter() {

		getAndVerifyLecturesByCourseId("?courseId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Type mismatch");
	}

	@Test
	public void getLecturesNotFound() {

		int courseIdNotFound = 123;

		getAndVerifyLecturesByCourseId("?courseId="+courseIdNotFound, OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getLecturesInvalidParameterNegativeValue() {

		int courseIdInvalid = -1;

		getAndVerifyLecturesByCourseId("?courseId=" + courseIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/lecture")
				.jsonPath("$.message").isEqualTo("Invalid courseId: " + courseIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyLecturesByCourseId(int courseId, HttpStatus expectedStatus) {
		return getAndVerifyLecturesByCourseId("?courseId=" + courseId, expectedStatus);
	}
	private WebTestClient.BodyContentSpec getAndVerifyLecturesByCourseId(String courseIdQuery, HttpStatus expectedStatus){
		return client.get()
				.uri("/lecture" + courseIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyLecture(int courseId, int lectureId, HttpStatus expectedStatus){
		Lecture recommendation = new Lecture(1, courseId, "Lecture 1", 7, "SA");
		return client.post()
				.uri("/lecture")
				.body(just(recommendation), Lecture.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private  WebTestClient.BodyContentSpec deleteAndVerifyLecturesByCourseId(int courseId, HttpStatus expectedStatus){
		return client.delete()
				.uri("/lecture?courseId=" + courseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
}
