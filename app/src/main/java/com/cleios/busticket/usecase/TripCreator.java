package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;

public class TripCreator {
    private final TripRepository tripRepository;

    public TripCreator(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createTrip(Trip trip, final ResultCallback<Trip, ErrorType> callback) {
        tripRepository.createTrip(trip, callback);
    }
}
