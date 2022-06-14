package com.distributed.systems.api.core.course;

import java.util.Date;

public class Course {

    private int courseId;
    private String courseTitle;
    private String courseDetails;
    private String language;
    private Date courseCreatedDate;
    private Date getCourseLastUpdatedDate;
    private double averageRating;
    private int numberOfStudents;
    private double price;
    private String priceCurrency;
    private String serviceAddress;

    public Course(){
    }

    public Course(int courseId, String courseTitle, String courseDetails, String language, Date courseCreatedDate, Date getCourseLastUpdatedDate, double averageRating, int numberOfStudents, double price, String serviceAddress, String priceCurrency) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDetails = courseDetails;
        this.language = language;
        this.courseCreatedDate = courseCreatedDate;
        this.getCourseLastUpdatedDate = getCourseLastUpdatedDate;
        this.averageRating = averageRating;
        this.numberOfStudents = numberOfStudents;
        this.price = price;
        this.serviceAddress = serviceAddress;
        this.priceCurrency = priceCurrency;
    }

    public Course(int courseId, String courseTitle, double price, String priceCurrency, String serviceAddress) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.price = price;
        this.priceCurrency = priceCurrency;
        this.serviceAddress = serviceAddress;
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

    public String getServiceAddress() {
        return serviceAddress;
    }
    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }
}
