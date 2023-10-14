package com.api.carparkslot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//@JsonPropertyOrder({"carParkNo", "address", "x_coord", "y_coord"})
public class CarPark {
    private String car_park_no;
    private String address;
    private double x_coord;
    private double y_coord;

    private double latitude;
    private double longitude;

    private int totalLots;
    private int lotsAvailable;

    @JsonIgnore
    public String getCarParkNo() {
        return car_park_no;
    }

    public void setCarParkNo(String car_park_no) {
        this.car_park_no = car_park_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonIgnore
    public double getX_coord() {
        return x_coord;
    }

    public void setX_coord(double x_coord) {
        this.x_coord = x_coord;
    }

    @JsonIgnore
    public double getY_coord() {
        return y_coord;
    }

    public void setY_coord(double y_coord) {
        this.y_coord = y_coord;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTotalLots() {
        return totalLots;
    }

    public void setTotalLots(int totalLots) {
        this.totalLots = totalLots;
    }

    public int getLotsAvailable() {
        return lotsAvailable;
    }

    public void setLotsAvailable(int lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }
}
