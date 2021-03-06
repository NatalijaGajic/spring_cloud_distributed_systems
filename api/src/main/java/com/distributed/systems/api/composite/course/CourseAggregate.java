package com.distributed.systems.api.composite.course;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;

import java.util.Date;
import java.util.List;

public class CourseAggregate {
    private int courseId;
    private String courseTitle;
    private String courseDetails;
    private String language;

    public String priceCurrency;
    private Date courseCreatedDate;
    private Date getCourseLastUpdatedDate;
    private double averageRating;
    private int numberOfStudents;
    private double price;
    private List<RatingSummary> ratings;
    private List<LectureSummary> lectures;
    private ServicesAddresses serviceAddress;
    private List<AuthorSummary> authors;

    private CourseAggregate(){}

    public CourseAggregate(int courseId, String courseTitle, String courseDetails, String language, Date courseCreatedDate, Date getCourseLastUpdatedDate, double averageRating, int numberOfStudents, double price, List<RatingSummary> ratings, List<LectureSummary> lectures, List<AuthorSummary> authors, ServicesAddresses serviceAddress) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDetails = courseDetails;
        this.language = language;
        this.courseCreatedDate = courseCreatedDate;
        this.getCourseLastUpdatedDate = getCourseLastUpdatedDate;
        this.averageRating = averageRating;
        this.numberOfStudents = numberOfStudents;
        this.price = price;
        this.ratings = ratings;
        this.lectures = lectures;
        this.authors = authors;
        this.serviceAddress = serviceAddress;
    }

    public CourseAggregate(int courseId, String courseTitle, String courseDetails, String language) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDetails = courseDetails;
        this.language = language;
    }

    public CourseAggregate(int courseId, String courseTitle, String courseDetails, String language, List<RatingSummary> ratings, List<LectureSummary> lectures, List<AuthorSummary> authors) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDetails = courseDetails;
        this.language = language;
        this.ratings = ratings;
        this.lectures = lectures;
        this.authors = authors;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(String courseDetails) {
        this.courseDetails = courseDetails;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getCourseCreatedDate() {
        return courseCreatedDate;
    }

    public void setCourseCreatedDate(Date courseCreatedDate) {
        this.courseCreatedDate = courseCreatedDate;
    }

    public Date getGetCourseLastUpdatedDate() {
        return getCourseLastUpdatedDate;
    }

    public void setGetCourseLastUpdatedDate(Date getCourseLastUpdatedDate) {
        this.getCourseLastUpdatedDate = getCourseLastUpdatedDate;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<RatingSummary> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingSummary> ratings) {
        this.ratings = ratings;
    }

    public List<LectureSummary> getLectures() {
        return lectures;
    }

    public void setLectures(List<LectureSummary> lectures) {
        this.lectures = lectures;
    }

    public ServicesAddresses getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(ServicesAddresses serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public List<AuthorSummary> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorSummary> authors) {
        this.authors = authors;
    }
}
