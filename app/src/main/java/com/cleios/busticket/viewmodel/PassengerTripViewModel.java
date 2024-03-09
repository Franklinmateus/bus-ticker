package com.cleios.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.R;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.usecase.TripDeleter;
import com.cleios.busticket.usecase.TripFinder;

import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class PassengerTripViewModel extends ViewModel {

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public TripFinder tripFinder;
    public TripDeleter tripDeleter;

    public PassengerTripViewModel(TripFinder tripFinder, TripDeleter tripDeleter) {
        this.tripFinder = tripFinder;
        this.tripDeleter = tripDeleter;
    }

    public void findAllReservations() {
        tripFinder.findAllPassengerTrips(result -> {
            if (result.data != null) {
                tripsLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

    public static final ViewModelInitializer<PassengerTripViewModel> initializer = new ViewModelInitializer<>(
            PassengerTripViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new PassengerTripViewModel(app.appModule.tripFinder, app.appModule.tripDeleter);
            }
    );

    public MutableLiveData<DataOrError<Boolean, ErrorType>> cancelReservation(String tripId) {
        MutableLiveData<DataOrError<Boolean, ErrorType>> result = new MutableLiveData<>();
        tripDeleter.cancelReservation(tripId, result::postValue);
        return result;
    }
}