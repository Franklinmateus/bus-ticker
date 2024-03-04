package com.cleios.busticket.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private Date date;
    private String recurrence;
    private List<TripStop> stops;
    private int seats;
    private int availableSeats;
    private String tripIdentificator;
    private String ownerId;
    private String tripId;
    private List<String> passengers = new ArrayList<>();

    public Trip(String origin, String destination, String departureTime, String arrivalTime, Date date, String recurrence, List<TripStop> stops, int seats) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.date = date;
        this.recurrence = recurrence;
        this.stops = stops;
        this.seats = seats;
        this.availableSeats = seats;
    }

    public Trip() {
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public List<TripStop> getStops() {
        return stops;
    }

    public void setStops(List<TripStop> stops) {
        this.stops = stops;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getTripIdentificator() {
        return tripIdentificator;
    }

    public void setTripIdentificator(String tripIdentificator) {
        this.tripIdentificator = tripIdentificator;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }
}
