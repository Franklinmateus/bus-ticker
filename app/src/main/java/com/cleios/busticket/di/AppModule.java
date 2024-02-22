package com.cleios.busticket.di;

import com.cleios.busticket.data.AccountRepository;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.data.TripRepository;
import com.cleios.busticket.usecase.TripCreator;
import com.cleios.busticket.usecase.TripFinder;

import java.util.concurrent.ExecutorService;

public class AppModule {
    private final ExecutorService executorService;
    public AuthRepository authRepository;
    public AccountRepository accountRepository;
    public TripRepository tripRepository;
    public TripCreator tripCreator;
    public TripFinder tripFinder;

    public AppModule(ExecutorService executorService) {
        this.executorService = executorService;
        this.authRepository = new AuthRepository(this.executorService);
        this.tripRepository = new TripRepository(this.executorService);
        this.accountRepository = new AccountRepository(this.executorService);
        this.tripCreator = new TripCreator(this.tripRepository);
        this.tripFinder = new TripFinder(this.tripRepository);
    }

}

