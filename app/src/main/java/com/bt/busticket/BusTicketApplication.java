package com.bt.busticket;

import android.app.Application;
import com.bt.busticket.di.AppModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BusTicketApplication extends Application {
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    public AppModule appModule;

    @Override
    public void onCreate() {
        super.onCreate();
        appModule = new AppModule(executorService, this);
    }
}
