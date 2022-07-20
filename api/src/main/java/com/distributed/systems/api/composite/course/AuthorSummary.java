package com.distributed.systems.api.composite.course;

public class AuthorSummary {
    private int authorId;
    private String fullName;
    private String country;
    private int numberOfLectures;

    public AuthorSummary(){

    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getNumberOfLectures() {
        return numberOfLectures;
    }

    public void setNumberOfLectures(int numberOfLectures) {
        this.numberOfLectures = numberOfLectures;
    }

    public AuthorSummary(int authorId, String fullName, String country, int numberOfLectures) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.country = country;
        this.numberOfLectures = numberOfLectures;
    }
}
