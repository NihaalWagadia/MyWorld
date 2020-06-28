package com.example.myworld.model;

import android.net.Uri;

import com.google.gson.internal.$Gson$Preconditions;

public class UserLocation {
    private Double lati;
    private Double longi;
    private String userId;
    private String imageId;
    private String userName;
    private String address;
    private String locate;

    public UserLocation(Double lati, Double longi, String userId, String imageId, String userName, String address, String locate) {
        this.lati = lati;
        this.longi = longi;
        this.userId = userId;
        this.imageId = imageId;
        this.userName = userName;
        this.address = address;
        this.locate = locate;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserLocation() {
    }

    public Double getLati() {
        return lati;
    }

    public void setLati(Double lati) {
        this.lati = lati;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "lati=" + lati +
                ", longi=" + longi +
                ", userId='" + userId + '\'' +
                ", imageId='" + imageId + '\'' +
                ", userName='" + userName + '\'' +
                ", address='" + address + '\'' +
                ", locate='" + locate + '\'' +
                '}';
    }
}
