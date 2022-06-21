package com.distributed.systems.coursecompositeservice.services;

import com.distributed.systems.api.composite.course.*;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseCompositeServiceImpl implements CourseCompositeService {

    private final ServiceUtil serviceUtil;
    private CourseCompositeIntegration integration;

    @Autowired
    public CourseCompositeServiceImpl(ServiceUtil serviceUtil, CourseCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public CourseAggregate getCourse(int courseId) {
        Course course = integration.getCourse(courseId);
        if(course == null) throw new NotFoundException("No course found for courseId: " + courseId);
        List<Lecture> lectures = integration.getLectures(courseId);
        List<Rating> ratings = integration.getRatings(courseId); //TODO: get fullName for ratinfs

        return createCourseAggregate(course, lectures, ratings);
    }

    private CourseAggregate createCourseAggregate(Course course, List<Lecture> lectures, List<Rating> ratings){

        List<RatingSummary> ratingsSummaries = ratings == null? null :
                ratings.stream().map(r -> new RatingSummary(
                        r.getRatingId(),
                        null,
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
