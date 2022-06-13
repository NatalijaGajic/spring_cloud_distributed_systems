package com.distributed.systems.api.core.user;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String serviceAddress;

    public User(){}

    public User(int userId, String fullName, String email, String password, String serviceAddress) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.serviceAddress = serviceAddress;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
