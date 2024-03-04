package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.ErrorType;

public class TripDeleter {
    private final TripRepository tripRepository;

    public TripDeleter(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void removeTripByIdentificator(String identificator,  final ResultCallback<Boolean, ErrorType> callback) {
        tripRepository.removeTripByIdentificator(identificator, callback);
    }

    public void cancelReservation(String tripId,  final ResultCallback<Boolean, ErrorType> callback) {
        tripRepository.cancelReservation(tripId, callback);
    }
}
