package com.cleios.busticket.di;

import com.cleios.busticket.data.AccountRepository;
import com.cleios.busticket.data.AuthRepository;

import java.util.concurrent.ExecutorService;

public class AppModule {
    private final ExecutorService executorService;
    public AuthRepository authRepository;
    public AccountRepository accountRepository;

    public AppModule(ExecutorService executorService) {
        this.executorService = executorService;
        this.authRepository = new AuthRepository(this.executorService);
        this.accountRepository = new AccountRepository(this.executorService);
    }

}

