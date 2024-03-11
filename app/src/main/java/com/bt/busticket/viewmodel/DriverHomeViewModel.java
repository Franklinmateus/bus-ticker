package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.R;
import com.bt.busticket.data.AuthRepository;
import com.bt.busticket.model.Trip;
import com.bt.busticket.usecase.TripFinder;
import com.bt.busticket.util.DateUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class DriverHomeViewModel extends ViewModel {
    FirebaseFirestore mFirestore;
    AuthRepository authRepository;
    public TripFinder tripFinder;
    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public DriverHomeViewModel(AuthRepository authRepository, TripFinder tripFinder) {
        this.authRepository = authRepository;
        this.tripFinder = tripFinder;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static final ViewModelInitializer<DriverHomeViewModel> initializer = new ViewModelInitializer<>(
            DriverHomeViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new DriverHomeViewModel(app.appModule.authRepository, app.appModule.tripFinder);
            }
    );

    public void findAllNext() {
        tripFinder.findNextTripsByOwner(result -> {
            if (result.data != null) {
                var currentDate = LocalDate.now();
                var list = result.data.stream().filter(v -> currentDate.minusDays(1).isBefore(DateUtil.asLocalDate(v.getDate()))).sorted(Comparator.comparing(Trip::getDate)).collect(Collectors.toList());
                tripsLiveData.postValue(list);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

}