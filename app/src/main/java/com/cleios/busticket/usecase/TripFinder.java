package com.cleios.busticket.usecase;

import com.cleios.busticket.data.ResultCallback;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TripFinder {
    private final TripRepository tripRepository;

    public TripFinder(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void findAllTripsByOwner(final ResultCallback<List<Trip>, ErrorType> callback) {
        tripRepository.findAllTripsByOwner(callback);
    }

    public void findNextTripsByOwner(final ResultCallback<List<Trip>, ErrorType> callback) {
        findAllTripsByOwner(result -> {
            if (result.data != null && !result.data.isEmpty()) {
                var currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                var list = result.data.stream().filter(v -> {
                    LocalDate tripDate = LocalDate.parse(v.getDate(), formatter);
                    return currentDate.minusDays(1).isBefore(tripDate);
                }).collect(Collectors.toList());

                callback.onComplete(new DataOrError<>(list, null));
            } else {
                callback.onComplete(result);
            }
        });
    }
}