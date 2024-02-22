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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class DriverTripViewModel extends ViewModel {

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public TripFinder tripFinder;
    public TripDeleter tripDeleter;

    public DriverTripViewModel(TripFinder tripFinder, TripDeleter tripDeleter) {
        this.tripFinder = tripFinder;
        this.tripDeleter = tripDeleter;
    }

    public void findAll() {
        tripFinder.findAllTripsByOwner(result -> {
            if (result.data != null) {

                var list = result.data.stream()
                        .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Trip::getTripIdentificator))),
                                ArrayList::new));
                tripsLiveData.postValue(list);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

    public static final ViewModelInitializer<DriverTripViewModel> initializer = new ViewModelInitializer<>(
            DriverTripViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new DriverTripViewModel(app.appModule.tripFinder, app.appModule.tripDeleter);
            }
    );

    public MutableLiveData<DataOrError<Boolean, ErrorType>> removeTripByIdentificator(String tripIdentificator) {
        MutableLiveData<DataOrError<Boolean, ErrorType>> result = new MutableLiveData<>();
        tripDeleter.removeTripByIdentificator(tripIdentificator, result::postValue);
        return result;
    }
}