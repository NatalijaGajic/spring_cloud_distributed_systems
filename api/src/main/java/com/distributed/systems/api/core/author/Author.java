package com.distributed.systems.api.core.author;

public class Author {
    private int authorId;
    private int courseId;
    private String fullName;
    private String country;
    private int numberOfLectures;
    private String serviceAddress;

    public Author(){

    }

    public Author(int authorId, int courseId, String fullName, String country, int numberOfLectures, String serviceAddress) {
        this.authorId = authorId;
        this.courseId = courseId;
        this.fullName = fullName;
        this.country = country;
        this.numberOfLectures = numberOfLectures;
        this.serviceAddress = serviceAddress;
    }

    public Author(int authorId, int courseId, String fullName, String country, int numberOfLectures) {
        this.authorId = authorId;
        this.courseId = courseId;
        this.fullName = fullName;
        this.country = country;
        this.numberOfLectures = numberOfLectures;
    }

    public Author(int authorId, String fullName, String country) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.country = country;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getNumberOfLectures() {
        return numberOfLectures;
    }

    public void setNumberOfLectures(int numberOfLectures) {
        this.numberOfLectures = numberOfLectures;
    }
}
