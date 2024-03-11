package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.R;
import com.bt.busticket.model.Trip;
import com.bt.busticket.model.TripStop;
import com.bt.busticket.usecase.TripCreator;
import com.bt.busticket.util.DateUtil;

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
        var mDate = DateUtil.getDateFromString(date);
        var trip = new Trip(origin, destination, departure, arrival, mDate, recurrence, stops, seats);
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