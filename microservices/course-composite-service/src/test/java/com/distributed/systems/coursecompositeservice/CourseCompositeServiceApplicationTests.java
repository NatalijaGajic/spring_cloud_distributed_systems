package com.distributed.systems.coursecompositeservice;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.coursecompositeservice.services.CourseCompositeIntegration;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class CourseCompositeServiceApplicationTests {

	private static final int COURSE_ID_OK = 1;
	private static final int COURSE_ID_NOT_FOUND = 2;
	private static final int COURSE_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private CourseCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getCourse(COURSE_ID_OK)).
				thenReturn(new Course(COURSE_ID_OK, "title", 5, "$", "mock-address"));
		when(compositeIntegration.getLectures(COURSE_ID_OK)).
				thenReturn(singletonList(new Lecture(1, COURSE_ID_OK, "title", "details", 1, 7)));
		when(compositeIntegration.getRatings(COURSE_ID_OK)).
				thenReturn(singletonList(new Rating(1, COURSE_ID_OK, 2, 5, "content", "mock address")));

		when(compositeIntegration.getCourse(COURSE_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + COURSE_ID_NOT_FOUND));

		when(compositeIntegration.getCourse(COURSE_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + COURSE_ID_INVALID));
	}

	@Test
	public void getCourseById() {

		client.get()
				.uri("/course-composite/" + COURSE_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.courseId").isEqualTo(COURSE_ID_OK)
				.jsonPath("$.lectures.length()").isEqualTo(1)
				.jsonPath("$.ratings.length()").isEqualTo(1);
	}

	@Test
	public void getCourseNotFound() {

		client.get()
				.uri("/course-composite/" + COURSE_ID_NOT_FOUND)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/course-composite/" + COURSE_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + COURSE_ID_NOT_FOUND);
	}

	@Test
	public void getCourseInvalidInput() {

		client.get()
				.uri("/course-composite/" + COURSE_ID_INVALID)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/course-composite/" + COURSE_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + COURSE_ID_INVALID);
	}

}
