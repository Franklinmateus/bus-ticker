package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.R;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;
import com.bt.busticket.model.Trip;
import com.bt.busticket.usecase.TripDeleter;
import com.bt.busticket.usecase.TripFinder;
import com.bt.busticket.usecase.TripReservationCreator;

import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class SearchTripsViewModel extends ViewModel {

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();
    public TripFinder tripFinder;
    public TripDeleter tripDeleter;
    TripReservationCreator tripReservationCreator;
    public SearchTripsViewModel(TripFinder tripFinder, TripDeleter tripDeleter, TripReservationCreator tripReservationCreator) {
        this.tripFinder = tripFinder;
        this.tripDeleter = tripDeleter;
        this.tripReservationCreator = tripReservationCreator;
    }

    public void findAll() {
        tripFinder.findAllAvailableTrips(result -> {
            if (result.data != null) {
                tripsLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

    public static final ViewModelInitializer<SearchTripsViewModel> initializer = new ViewModelInitializer<>(
            SearchTripsViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new SearchTripsViewModel(app.appModule.tripFinder, app.appModule.tripDeleter, app.appModule.tripReservationCreator);
            }
    );


    public MutableLiveData<DataOrError<Boolean, ErrorType>> createReservation(Trip trip) {
        MutableLiveData<DataOrError<Boolean, ErrorType>> result = new MutableLiveData<>();
        tripReservationCreator.createReservation(trip, result::postValue);
        return result;
    }
}