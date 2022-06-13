package com.distributed.systems.api.composite.course;

import java.util.Date;

public class RatingSummary {

    private int ratingId;
    private String userFullName;
    private int starRating;
    private String text;
    private Date ratingCreatedDate;

    public RatingSummary(){}

    public RatingSummary(int ratingId, String userfullName, int starRating, String text, Date ratingCreatedDate) {
        this.ratingId = ratingId;
        this.userFullName = userfullName;
        this.starRating = starRating;
        this.text = text;
        this.ratingCreatedDate = ratingCreatedDate;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
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
}
