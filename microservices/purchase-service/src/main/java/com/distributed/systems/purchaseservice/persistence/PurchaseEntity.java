package com.distributed.systems.purchaseservice.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="purchases")
@CompoundIndex(name = "crs-prch-id", unique = true, def = "{'courseId': 1, 'purchaseId' : 1}")
public class PurchaseEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int purchaseId;
    private int userId;
    private int courseId;
    private Date purchasedDate;
    private double purchasedPrice;
    private String purchasedPriceCurrency;
    private String serviceAddress;

    public PurchaseEntity(){}


    public PurchaseEntity(int purchaseId, int userId, int courseId, Date purchasedDate, double purchasedPrice, String purchasedPriceCurrency, String serviceAddress) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.courseId = courseId;
        this.purchasedDate = purchasedDate;
        this.purchasedPrice = purchasedPrice;
        this.purchasedPriceCurrency = purchasedPriceCurrency;
        this.serviceAddress = serviceAddress;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Date purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public double getPurchasedPrice() {
        return purchasedPrice;
    }

    public void setPurchasedPrice(double purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getPurchasedPriceCurrency() {
        return purchasedPriceCurrency;
    }

    public void setPurchasedPriceCurrency(String purchasedPriceCurrency) {
        this.purchasedPriceCurrency = purchasedPriceCurrency;
    }
}
