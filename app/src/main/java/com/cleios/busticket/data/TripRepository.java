package com.cleios.busticket.data;

import android.util.Log;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

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

    public void findAllTripsByOwner(ResultCallback<List<Trip>, ErrorType> callback) {
        executor.execute(() -> {
            try {
                var firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    callback.onComplete(new DataOrError<>(null, ErrorType.UNAUTHORIZED));
                    return;
                }
                String userUid = firebaseUser.getUid();
                List<Trip> trips = new ArrayList<>();
                mFirestore.collection("public-travel").whereEqualTo("ownerId", userUid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            trips.add(document.toObject(Trip.class));
                        }
                        callback.onComplete(new DataOrError<>(trips, null));
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        callback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
                    }
                });
            } catch (Exception e) {
                callback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
            }
        });
    }

    public void removeTripByIdentificator(String identificator, ResultCallback<Boolean, ErrorType> callback) {
        executor.execute(() -> {
            var batch = mFirestore.batch();
            mFirestore.collection("public-travel").whereEqualTo("tripIdentificator", identificator).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        batch.delete(document.getReference());
                    }
                    batch.commit().addOnCompleteListener(deletionTask -> {
                        if (deletionTask.isSuccessful()) {
                            callback.onComplete(new DataOrError<>(true, null));
                        } else {
                            callback.onComplete(new DataOrError<>(false, ErrorType.GENERIC_ERROR));
                        }
                    });
                } else {
                    callback.onComplete(new DataOrError<>(false, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }
}
