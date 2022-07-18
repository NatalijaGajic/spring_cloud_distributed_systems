package com.distributed.systems.coursecompositeservice;

import com.distributed.systems.api.composite.course.CourseAggregate;
import com.distributed.systems.api.composite.course.LectureSummary;
import com.distributed.systems.api.composite.course.RatingSummary;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.coursecompositeservice.services.CourseCompositeIntegration;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment=RANDOM_PORT,
		classes = {CourseCompositeServiceApplication.class, TestSecurityConfig.class },
		properties = {"spring.main.allow-bean-definition-overriding=true","eureka.client.enabled=false","spring.cloud.config.enabled=false"})
public class CourseCompositeServiceApplicationTests {

	//TODO: appContext not loaded fail
	private static final int COURSE_ID_OK = 1;
	private static final int COURSE_ID_NOT_FOUND = 2;
	private static final int COURSE_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private CourseCompositeIntegration compositeIntegration;

	@Before
	public void setUp() {

		when(compositeIntegration.getCourse(COURSE_ID_OK)).
				thenReturn(Mono.just(new Course(COURSE_ID_OK, "title", 5, "$", "mock-address")) );
		when(compositeIntegration.getLectures(COURSE_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Lecture(1, COURSE_ID_OK, "title", "details", 1, 7))));
		when(compositeIntegration.getRatings(COURSE_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Rating(1, COURSE_ID_OK, 2, 5, "content", "mock address"))));

		when(compositeIntegration.getCourse(COURSE_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + COURSE_ID_NOT_FOUND));

		when(compositeIntegration.getCourse(COURSE_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + COURSE_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}



	@Test
	public void getCourseById() {

		getAndVerifyCourse(COURSE_ID_OK, OK)
				.jsonPath("$.courseId").isEqualTo(COURSE_ID_OK)
				.jsonPath("$.lectures.length()").isEqualTo(1)
				.jsonPath("$.ratings.length()").isEqualTo(1);
	}

	@Test
	public void getCourseNotFound() {

		getAndVerifyCourse(COURSE_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/course-composite/" + COURSE_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + COURSE_ID_NOT_FOUND);
	}


	@Test
	public void getCourseInvalidInput() {

		getAndVerifyCourse(COURSE_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/course-composite/" + COURSE_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + COURSE_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyCourse(int courseId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/course-composite/" + courseId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}


}
