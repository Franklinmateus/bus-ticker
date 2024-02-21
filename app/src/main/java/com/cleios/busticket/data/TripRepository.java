package com.cleios.busticket.data;

import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

public class TripRepository {
    private final FirebaseFirestore mFirestore;
    private final FirebaseAuth mFirebaseAuth;
    private final Executor executor;

    public TripRepository(Executor executor) {
        this.executor = executor;
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void createTrip(Trip trip, final ResultCallback<Trip, ErrorType> callback) {
        executor.execute(() -> {
            var firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                callback.onComplete(new DataOrError<>(null, ErrorType.UNAUTHORIZED));
                return;
            }
            String userUid = firebaseUser.getUid();
            trip.setOwnerId(userUid);

            var doc = mFirestore.collection("public-travel").document();

            trip.setTripId(doc.getId());

            doc.set(trip).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onComplete(new DataOrError<>(trip, null));
                    createTripRecurrences(trip, r -> {
                    });
                } else {
                    callback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }

    private void createTripRecurrences(Trip trip, final ResultCallback<Boolean, ErrorType> callback) {
        executor.execute(() -> {
            WriteBatch batch;
            if (trip.getRecurrence().equals("DAILY")) {
                batch = buildRecurrenceBatch(trip, trip.getRecurrence(), 30);
            } else if (trip.getRecurrence().equals("WEEKLY")) {
                batch = buildRecurrenceBatch(trip, trip.getRecurrence(), 8);
            } else {
                return;
            }
            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onComplete(new DataOrError<>(true, null));
                } else {
                    callback.onComplete(new DataOrError<>(false, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }

    private WriteBatch buildRecurrenceBatch(Trip trip, String type, int numberOfRecurrences) {
        var startDate = trip.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(startDate, formatter);

        WriteBatch batch = mFirestore.batch();

        for (int i = 1; i <= numberOfRecurrences; i++) {
            var doc = mFirestore.collection("public-travel").document();
            trip.setTripId(doc.getId());

            var newDate = type.equals("DAILY") ? date.plusDays(i) : date.plusWeeks(i);

            trip.setDate(newDate.format(formatter));
            batch.set(doc, trip);
        }
        return batch;
    }
}
