package com.distributed.systems.coursecompositeservice.services;

import com.distributed.systems.api.composite.course.*;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseCompositeServiceImpl implements CourseCompositeService {

    private final ServiceUtil serviceUtil;
    private CourseCompositeIntegration integration;

    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeIntegration.class);

    @Autowired
    public CourseCompositeServiceImpl(ServiceUtil serviceUtil, CourseCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public void createCompositeCourse(CourseAggregate body) {
        try {

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

            LOG.debug("createCompositeCourse: composite entites created for courseId: {}", body.getCourseId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeCourse failed", re);
            throw re;
        }
    }

    @Override
    public CourseAggregate getCourse(int courseId) {
        LOG.debug("getCourse: lookup a course aggregate for courseId: {}", courseId);
        //TODO: use non-blocking API
        Course course = new Course(1, "title", 80, "$");
        //Course course = integration.getCourse(courseId);
        if(course == null) throw new NotFoundException("No course found for courseId: " + courseId);
        List<Lecture> lectures = new ArrayList<>();
        //List<Lecture> lectures = integration.getLectures(courseId);
        List<Rating> ratings = integration.getRatings(courseId); //TODO: get fullName for ratings
        LOG.debug("getCourse: aggregate entity found for courseId: {}", courseId);

        return createCourseAggregate(course, lectures, ratings);
    }

    @Override
    public void deleteCompositeCourse(int courseId) {
        LOG.debug("deleteCompositeCourse: Deletes a course aggregate for courseId: {}", courseId);

        integration.deleteCourse(courseId);

        integration.deleteLectures(courseId);

        integration.deleteRatings(courseId);

        LOG.debug("deleteCompositeCourse: aggregate entities deleted for courseId: {}", courseId);
    }

    private CourseAggregate createCourseAggregate(Course course, List<Lecture> lectures, List<Rating> ratings){

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

        String courseAddress = course.getServiceAddress();
        String ratingsAddress = (ratings != null && ratings.size() > 0)? ratings.get(0).getServiceAddress():"";
        String lecturesAddress = (lectures != null && lectures.size() > 0)? lectures.get(0).getServiceAddress():"";
        ServicesAddresses servicesAddresses = new ServicesAddresses(courseAddress, ratingsAddress, lecturesAddress, serviceUtil.getServiceAddress());


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
                servicesAddresses
        );
    }


}
