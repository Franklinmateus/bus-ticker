package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;

import java.util.List;

public class TripFinder {
    private final TripRepository tripRepository;

    public TripFinder(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void findAllTripsByOwner(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findAllByOwner(callback);
    }

    public void findNextTripsByOwner(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findAllByOwner(result -> {
            if (result.data != null) {
                callback.onComplete(new DataOrError<>(result.data, null));
            } else {
                callback.onComplete(result);
            }
        });
    }

    public void findAllPassengerTrips(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findPassengerTrips(callback);
    }

    public void findAllAvailableTrips(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findAllAvailableTrips(callback);
    }
}
