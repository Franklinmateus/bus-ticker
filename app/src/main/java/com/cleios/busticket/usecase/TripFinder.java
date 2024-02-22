package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;

import java.util.List;

public class TripFinder {
    private final TripRepository tripRepository;

    public TripFinder(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void findAllTripsByOwner(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findAllTripsByOwner(callback);
    }
}
