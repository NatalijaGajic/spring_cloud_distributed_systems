package com.distributed.systems.api.core.lecture;

public class Lecture {

    private int lectureId;
    private int courseId;
    private String lectureTitle;
    private String lectureDetails;
    private int lectureOrder;
    private int durationInMinutes;

    private String serviceAddress;

    public Lecture(){

    }

    public Lecture(int lectureId, int courseId, String lectureTitle, String lectureDetails, int lectureOrder, int durationInMinutes) {
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
}
