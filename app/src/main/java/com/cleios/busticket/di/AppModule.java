package com.cleios.busticket.di;

import android.content.Context;
import com.cleios.busticket.data.AccountRepository;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.data.FirebaseStorageService;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.usecase.TripCreator;
import com.cleios.busticket.usecase.TripDeleter;
import com.cleios.busticket.usecase.TripFinder;
import com.cleios.busticket.usecase.TripReservationCreator;

import java.util.concurrent.ExecutorService;

public class AppModule {
    private final ExecutorService executorService;
    public AuthRepository authRepository;
    public AccountRepository accountRepository;
    public TripRepository tripRepository;
    public TripCreator tripCreator;
    public TripFinder tripFinder;
    public TripDeleter tripDeleter;
    public TripReservationCreator tripReservationCreator;
    public FirebaseStorageService firebaseStorageService;

    public AppModule(ExecutorService executorService, Context context) {
        this.executorService = executorService;
        this.authRepository = new AuthRepository(this.executorService);
        this.tripRepository = new TripRepository(this.executorService);
        this.accountRepository = new AccountRepository(this.executorService);
        this.tripCreator = new TripCreator(this.tripRepository);
        this.tripFinder = new TripFinder(this.tripRepository);
        this.tripDeleter = new TripDeleter(this.tripRepository);
        this.tripReservationCreator = new TripReservationCreator(this.tripRepository);
        this.firebaseStorageService = new FirebaseStorageService(context, executorService);
    }
}

