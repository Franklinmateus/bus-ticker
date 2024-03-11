package com.bt.busticket.model;

public class TripStop {
    private String location;
    private String time;

    public TripStop(String location, String time) {
        this.location = location;
        this.time = time;
    }

    public TripStop() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
