package com.bt.busticket.usecase;

import com.bt.busticket.data.ResultCallback;
import com.bt.busticket.data.TripRepository;
import com.bt.busticket.model.ErrorType;
import com.bt.busticket.model.Trip;

public class TripCreator {
    private final TripRepository tripRepository;

    public TripCreator(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createTrip(Trip trip, final ResultCallback<Trip, ErrorType> callback) {
        tripRepository.createTrip(trip, callback);
    }
}
