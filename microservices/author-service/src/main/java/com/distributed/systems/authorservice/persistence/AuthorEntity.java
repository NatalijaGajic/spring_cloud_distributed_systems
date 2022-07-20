package com.distributed.systems.authorservice.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authors")
@CompoundIndex(name = "crs-auth-id", unique = true, def = "{'courseId': 1, 'authorId' : 1}")
public class AuthorEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int authorId;
    private int courseId;
    private String fullName;
    private String country;
    private int numberOfLectures;
    private String serviceAddress;

    public AuthorEntity(){


    }

    public AuthorEntity(String id, Integer version, int authorId, int courseId, String fullName, String country, int numberOfLectures, String serviceAddress) {
        this.id = id;
        this.version = version;
        this.authorId = authorId;
        this.courseId = courseId;
        this.fullName = fullName;
        this.country = country;
        this.numberOfLectures = numberOfLectures;
        this.serviceAddress = serviceAddress;
    }

    public AuthorEntity(int authorId, int courseId, String fullName, String country, int numberOfLectures) {
        this.authorId = authorId;
        this.courseId = courseId;
        this.fullName = fullName;
        this.country = country;
        this.numberOfLectures = numberOfLectures;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
