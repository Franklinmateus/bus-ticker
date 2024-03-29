package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.R;
import com.bt.busticket.data.AuthRepository;
import com.bt.busticket.model.Trip;
import com.bt.busticket.usecase.TripFinder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Comparator;
import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class PassengerHomeViewModel extends ViewModel {
    FirebaseFirestore mFirestore;
    AuthRepository authRepository;
    public TripFinder tripFinder;
    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public PassengerHomeViewModel(AuthRepository authRepository, TripFinder tripFinder) {
        this.authRepository = authRepository;
        this.tripFinder = tripFinder;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static final ViewModelInitializer<PassengerHomeViewModel> initializer = new ViewModelInitializer<>(
            PassengerHomeViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new PassengerHomeViewModel(app.appModule.authRepository, app.appModule.tripFinder);
            }
    );

    public void findAll() {
        tripFinder.findAllPassengerTrips(result -> {
            if (result.data != null) {
                result.data.sort(Comparator.comparing(Trip::getDate));
                tripsLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }
}