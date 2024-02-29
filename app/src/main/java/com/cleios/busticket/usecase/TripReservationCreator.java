package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;

public class TripReservationCreator {
    private final TripRepository tripRepository;

    public TripReservationCreator(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createReservation(Trip trip, final ResultCallback<Boolean, ErrorType> callback) {
        tripRepository.createReservation(trip, callback);
    }
}
