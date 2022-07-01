package com.distributed.systems.lectureservice.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lectures")
@CompoundIndex(name = "crs-lect-id", unique = true, def = "{'courseId': 1, 'lectureId' : 1}")
public class LectureEntity {


    @Id
    private String id;

    @Version
    private Integer version;

    private int lectureId;
    private int courseId;
    private String lectureTitle;
    private String lectureDetails;
    private int lectureOrder;
    private int durationInMinutes;

    private String serviceAddress;

    public LectureEntity(){

    }

    public LectureEntity(int lectureId, int courseId, String lectureTitle, int durationInMinutes, String serviceAddress) {
        this.lectureId = lectureId;
        this.courseId = courseId;
        this.lectureTitle = lectureTitle;
        this.durationInMinutes = durationInMinutes;
        this.serviceAddress = serviceAddress;
    }

    public LectureEntity(int lectureId, int courseId, String lectureTitle, String lectureDetails, int lectureOrder, int durationInMinutes) {
        this.lectureId = lectureId;
        this.courseId = courseId;
        this.lectureTitle = lectureTitle;
        this.lectureDetails = lectureDetails;
        this.lectureOrder = lectureOrder;
        this.durationInMinutes = durationInMinutes;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public String getLectureDetails() {
        return lectureDetails;
    }

    public void setLectureDetails(String lectureDetails) {
        this.lectureDetails = lectureDetails;
    }

    public int getLectureOrder() {
        return lectureOrder;
    }

    public void setLectureOrder(int lectureOrder) {
        this.lectureOrder = lectureOrder;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
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
}
