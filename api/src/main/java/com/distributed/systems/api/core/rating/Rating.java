package com.distributed.systems.api.core.rating;

import java.util.Date;

public class Rating {

    private int ratingId;
    private int courseId;
    private int userId;
    private int starRating;
    private String text;
    private Date ratingCreatedDate;
    private String serviceAddress;

    public Rating(){}

    public Rating(int ratingId, int courseId, int userId, int starRating, String text, String serviceAddress) {
        this.ratingId = ratingId;
        this.courseId = courseId;
        this.userId = userId;
        this.starRating = starRating;
        this.text = text;
        this.serviceAddress = serviceAddress;
    }

    public Rating(int ratingId, int courseId, int userId, int starRating, String text, Date ratingCreatedDate, String serviceAddress) {
        this.ratingId = ratingId;
        this.courseId = courseId;
        this.userId = userId;
        this.starRating = starRating;
        this.text = text;
        this.ratingCreatedDate = ratingCreatedDate;
        this.serviceAddress = serviceAddress;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getRatingCreatedDate() {
        return ratingCreatedDate;
    }

    public void setRatingCreatedDate(Date ratingCreatedDate) {
        this.ratingCreatedDate = ratingCreatedDate;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
