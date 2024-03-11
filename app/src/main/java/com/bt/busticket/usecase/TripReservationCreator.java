package com.bt.busticket.usecase;

import com.bt.busticket.data.ResultCallback;
import com.bt.busticket.data.TripRepository;
import com.bt.busticket.model.ErrorType;
import com.bt.busticket.model.Trip;

public class TripReservationCreator {
    private final TripRepository tripRepository;

    public TripReservationCreator(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createReservation(Trip trip, final ResultCallback<Boolean, ErrorType> callback) {
        tripRepository.createReservation(trip, callback);
    }
}
