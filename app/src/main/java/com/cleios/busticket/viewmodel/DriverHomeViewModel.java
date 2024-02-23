package com.cleios.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.R;
import com.cleios.busticket.auth.AuthManager;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.usecase.TripFinder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public void signOut() {
        AuthManager.getInstance().signOut();
    }

    public static final ViewModelInitializer<DriverHomeViewModel> initializer = new ViewModelInitializer<>(
            DriverHomeViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new DriverHomeViewModel(app.appModule.authRepository, app.appModule.tripFinder);
            }
    );

    public void findAll() {
        tripFinder.findNextTripsByOwner(result -> {
            if (result.data != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                result.data.sort((obj1, obj2) -> {
                    LocalDate date1 = LocalDate.parse(obj1.getDate(), formatter);
                    LocalDate date2 = LocalDate.parse(obj2.getDate(), formatter);
                    return date1.compareTo(date2);
                });
                tripsLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }
}