package com.distributed.systems.coursecompositeservice.services;

import com.distributed.systems.api.composite.course.*;
import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseCompositeServiceImpl implements CourseCompositeService {

    private final ServiceUtil serviceUtil;
    private final CourseCompositeIntegration integration;

    private final SecurityContext nullSC = new SecurityContextImpl();

    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeIntegration.class);

    @Autowired
    public CourseCompositeServiceImpl(ServiceUtil serviceUtil, CourseCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<Void> createCompositeCourse(CourseAggregate body) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalCreateCompositeCourse(sc, body)).then();
    }

    private void internalCreateCompositeCourse(SecurityContext sc, CourseAggregate body) {

        try {

            logAuthorizationInfo(sc);

            LOG.debug("createCompositeCourse: creates a new composite entity for courseId: {}", body.getCourseId());

            Course course = new Course(body.getCourseId(), body.getCourseTitle(), body.getCourseDetails(), body.getLanguage(), body.getCourseCreatedDate(), body.getGetCourseLastUpdatedDate(), body.getAverageRating(), body.getNumberOfStudents(), body.getPrice(), null, body.getPriceCurrency());
            integration.createCourse(course);

            if (body.getRatings() != null) {
                body.getRatings().forEach(r -> {
                    Rating rating = new Rating(r.getRatingId(), body.getCourseId(), r.getUserId(), r.getStarRating(), r.getText(), null);
                    integration.createRating(rating);
                });
            }

            if (body.getLectures() != null) {
                body.getLectures().forEach(l -> {
                    Lecture lecture = new Lecture(l.getLectureId(), body.getCourseId(), l.getLectureTitle(), l.getLectureDetails(), l.getLectureOrder(), l.getDurationInMinutes());
                    integration.createLecture(lecture);
                });
            }

            if (body.getAuthors() != null) {
                body.getAuthors().forEach(l -> {
                    Author author = new Author(l.getAuthorId(), body.getCourseId(), l.getFullName(), l.getCountry(), l.getNumberOfLectures() );
                    integration.createAuthor(author);
                });
            }

            LOG.debug("createCompositeCourse: composite entites created for courseId: {}", body.getCourseId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeCourse failed: {}", re.toString());
            throw re;
        }

    }

    /**
     * Apply fallback logic when circuit breaker is open;
     * Catch CallNotPermittedException (signals that the CircuitBreaker is HALF_OPEN or OPEN and a call is not permitted to be executed.)
     *
     * @param courseId
     * @param delay - number of seconds; causes the service to delay the response
     * @param faultPercent - probability; causes the service to throw an exc
     * @return
     */

    @Override
    public Mono<CourseAggregate> getCourseComposite(int courseId, int delay, int faultPercent) {

        LOG.debug("getCourse: lookup a course aggregate for courseId: {}", courseId);

        return Mono.zip(
                        values -> createCourseAggregate((SecurityContext) values[0], (Course) values[1], (List<Lecture>) values[2], (List<Rating>) values[3], (List<Author>) values[4]),
                        ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
                        integration.getCourse(courseId, delay, faultPercent)
                                .onErrorReturn(CallNotPermittedException.class, getCourseFallbackValue(courseId)),
                        integration.getLectures(courseId).collectList(),
                        integration.getRatings(courseId).collectList(),
                        integration.getAuthors(courseId).collectList())
                .doOnError(ex -> LOG.warn("getCompositeCourse failed: {}", ex.toString()))
                .log();
    }

    @Override
    public Mono<Void> deleteCompositeCourse(int courseId) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalDeleteCompositeCourse(sc, courseId)).then();
    }

    private void internalDeleteCompositeCourse(SecurityContext sc, int courseId) {
        try {
            logAuthorizationInfo(sc);

            LOG.debug("deleteCompositeCourse: Deletes a course aggregate for courseId: {}", courseId);

            integration.deleteCourse(courseId);

            integration.deleteLectures(courseId);

            integration.deleteRatings(courseId);

            integration.deleteAuthors(courseId);

            LOG.debug("deleteCompositeCourse: aggregate entities deleted for courseId: {}", courseId);

        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeCourse failed: {}", re.toString());
            throw re;
        }

    }

    private Course getCourseFallbackValue(int courseId) {

        LOG.warn("Creating a fallback product for courseId = {}", courseId);

        if (courseId == 13) {
            String errMsg = "Course Id: " + courseId + " not found in fallback cache!";
            LOG.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new Course(courseId, "Fallback course", 0, null, serviceUtil.getServiceAddress());
    }


    private CourseAggregate createCourseAggregate(SecurityContext sc, Course course, List<Lecture> lectures, List<Rating> ratings, List<Author> authors){

        logAuthorizationInfo(sc);

        List<RatingSummary> ratingsSummaries = ratings == null? null :
                ratings.stream().map(r -> new RatingSummary(
                        r.getRatingId(), r.getUserId(),
                        r.getStarRating(),
                        r.getText(),
                        r.getRatingCreatedDate()
                )).collect(Collectors.toList());

        List<LectureSummary> lecturesSummaries = lectures == null? null :
                lectures.stream().map(l -> new LectureSummary(
                        l.getLectureId(),
                        l.getCourseId(),
                        l.getLectureTitle(),
                        l.getLectureDetails(),
                        l.getLectureOrder(),
                        l.getDurationInMinutes()
                )).collect(Collectors.toList());

        List<AuthorSummary> authorSummaries = authors == null? null :
                authors.stream().map(a -> new AuthorSummary(
                        a.getAuthorId(),
                        a.getFullName(),
                        a.getCountry(),
                        a.getNumberOfLectures()
                )).collect(Collectors.toList());

        String courseAddress = course.getServiceAddress();
        String ratingsAddress = (ratings != null && ratings.size() > 0)? ratings.get(0).getServiceAddress():"";
        String lecturesAddress = (lectures != null && lectures.size() > 0)? lectures.get(0).getServiceAddress():"";
        String authorsAdresses = (authors != null && authors.size() > 0)? authors.get(0).getServiceAddress():"";
        ServicesAddresses servicesAddresses = new ServicesAddresses(courseAddress, ratingsAddress, lecturesAddress, authorsAdresses, serviceUtil.getServiceAddress());


        return new CourseAggregate(
                course.getCourseId(),
                course.getCourseTitle(),
                course.getCourseDetails(),
                course.getLanguage(),
                course.getCourseCreatedDate(),
                course.getGetCourseLastUpdatedDate(),
                course.getAverageRating(),
                course.getNumberOfStudents(),
                course.getPrice(),
                ratingsSummaries,
                lecturesSummaries,
                authorSummaries,
                servicesAddresses
        );
    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }

}
