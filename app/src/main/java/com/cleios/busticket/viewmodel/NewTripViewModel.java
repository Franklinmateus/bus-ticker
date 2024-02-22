package com.cleios.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.R;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.model.TripStop;
import com.cleios.busticket.usecase.TripCreator;

import java.util.List;
import java.util.UUID;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class NewTripViewModel extends ViewModel {

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();

    public MutableLiveData<Trip> tripLiveData = new MutableLiveData<>();

    public TripCreator tripCreator;

    public NewTripViewModel(TripCreator tripCreator) {
        this.tripCreator = tripCreator;
    }

    public void createTrip(String origin, String destination, String departure, String arrival, String date, String recurrence, List<TripStop> stops, int seats) {
        var trip = new Trip(origin, destination, departure, arrival, date, recurrence, stops, seats);
        trip.setTripIdentificator(UUID.randomUUID().toString());

        tripCreator.createTrip(trip, result -> {
            if (result.data != null) {
                tripLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

    public static final ViewModelInitializer<NewTripViewModel> initializer = new ViewModelInitializer<>(
            NewTripViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new NewTripViewModel(app.appModule.tripCreator);
            }
    );
}