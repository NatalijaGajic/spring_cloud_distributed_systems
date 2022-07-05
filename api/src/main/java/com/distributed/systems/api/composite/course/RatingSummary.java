package com.distributed.systems.api.composite.course;

import java.util.Date;

public class RatingSummary {

    private int ratingId;



    private int userId;
    private int starRating;
    private String text;
    private Date ratingCreatedDate;

    public RatingSummary(){}

    public RatingSummary(int ratingId, int userId, int starRating, String text, Date ratingCreatedDate) {
        this.ratingId = ratingId;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
