package com.distributed.systems.coursecompositeservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import com.distributed.systems.api.core.user.User;
import com.distributed.systems.api.core.user.UserService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CourseCompositeIntegration implements CourseService, LectureService, RatingService, UserService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeIntegration.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String courseServiceUrl;
    private final String lectureServiceUrl;
    private final String ratingServiceUrl;
    private final String userServiceUrl;

    @Autowired
    public CourseCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,

            @Value("${app.course-service.host}") String courseServiceHost,
            @Value("${app.course-service.port}") String courseServicePort,
            @Value("${app.lecture-service.host}") String lectureServiceHost,
            @Value("${app.lecture-service.port}") String lectureServicePort,
            @Value("${app.user-service.host}") String userServiceHost,
            @Value("${app.user-service.port}") String userServicePort,
            @Value("${app.rating-service.host}") String ratingServiceHost,
            @Value("${app.rating-service.port}") String ratingServicePort
    ) {

        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.courseServiceUrl = "http://" + courseServiceHost + ":" + courseServicePort + "/course";
        this.lectureServiceUrl = "http://" + lectureServiceHost + ":" + lectureServicePort + "/lecture";;
        this.ratingServiceUrl = "http://" + ratingServiceHost + ":" + ratingServicePort + "/rating";
        this.userServiceUrl = "http://" + userServiceHost + ":" + userServicePort + "/user";;
    }

    @Override
    public Course createCourse(Course body) {

        try {
            String url = courseServiceUrl;
            LOG.debug("Will post a new course to URL: {}", url);

            Course course = restTemplate.postForObject(url, body, Course.class);
            LOG.debug("Created a course with id: {}", course.getCourseId());

            return course;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Course getCourse(int courseId) {
        try {
            String url = courseServiceUrl + "/" + courseId;
            LOG.debug("Will call the getCourse API on URL: {}", url);

            Course course = restTemplate.getForObject(url, Course.class);
            LOG.debug("Found a course with id: {}", course.getCourseId());

            return course;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteCourse(int courseId) {
        try {
            String url = courseServiceUrl + "/" + courseId;
            LOG.debug("Will call the deleteCourse API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Lecture createLecture(Lecture body) {

        try {
            String url = lectureServiceUrl;
            LOG.debug("Will post a new lecture to URL: {}", url);

            Lecture lecture = restTemplate.postForObject(url, body, Lecture.class);
            LOG.debug("Created a lecture with id: {}", lecture.getLectureId());

            return lecture;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Lecture> getLectures(int courseId) {
        try{
            String url = lectureServiceUrl + "?courseId=" +courseId;
            LOG.debug("Will call getLectures API on URL: {}", url);
            List<Lecture> lectures = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Lecture>>(){}).getBody();
            LOG.debug("Found {} lectures for a course with id: {}", lectures.size(), courseId);
            return lectures;

        }catch (Exception ex){
            LOG.warn("Got an exception while requesting lectures, return zero lectures: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteLectures(int courseId) {
        try {
            String url = lectureServiceUrl + "?courseId=" + courseId;
            LOG.debug("Will call the deleteLectures API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Rating createRating(Rating body) {

        try {
            String url = ratingServiceUrl;
            LOG.debug("Will post a new rating to URL: {}", url);

            Rating rating = restTemplate.postForObject(url, body, Rating.class);
            LOG.debug("Created a rating with id: {}", rating.getRatingId());

            return rating;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Rating> getRatings(int courseId) {
        try{
            String url = ratingServiceUrl + "?courseId=" + courseId;
            LOG.debug("Will call getRatings API on URL: {}", url);
            List<Rating> ratings = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Rating>>(){}).getBody();
            LOG.debug("Found {} ratings for a course with id: {}", ratings.size(), courseId);
            return ratings;

        }catch (Exception ex){
            LOG.warn("Got an exception while requesting ratings, return zero ratings: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRatings(int courseId) {
        try {
            String url = ratingServiceUrl + "?courseId=" + courseId;
            LOG.debug("Will call the deleteRatings API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public User createUser(User body) {
        return null;
    }

    @Override
    public User getUser(int userId) {
        try{
            String url = courseServiceUrl + userId;
            LOG.debug("Will call getUser API on URL: {}", url);
            User user = restTemplate.getForObject(url, User.class);
            LOG.debug("Found a user with id: {}", user.getUserId());
            return user;

        }catch(HttpClientErrorException ex){ //TODO: shouldn't throw exceptions
            switch (ex.getStatusCode()){
                case NOT_FOUND:
                    throw new NotFoundException((getErrorMessage(ex)));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }

        }
    }

    @Override
    public void deleteUser(int userId) {

    }

    private String getErrorMessage(HttpClientErrorException ex){
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }
}
