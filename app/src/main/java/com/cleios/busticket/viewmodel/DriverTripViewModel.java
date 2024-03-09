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
import com.cleios.busticket.util.DateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class DriverTripViewModel extends ViewModel {

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public TripFinder tripFinder;
    public TripDeleter tripDeleter;

    public DriverTripViewModel(TripFinder tripFinder, TripDeleter tripDeleter) {
        this.tripFinder = tripFinder;
        this.tripDeleter = tripDeleter;
    }

    public void findAllTrips() {
        tripFinder.findAllTripsByOwner(result -> {
            if (result.data != null) {
                tripsLiveData.postValue(result.data);
            } else {
                errorLiveData.postValue(R.string.some_error_has_occurred);
            }
        });
    }

    public MutableLiveData<List<Trip>> findAllPastTrips() {
        MutableLiveData<List<Trip>> resulLiveData = new MutableLiveData<>();
        tripFinder.findAllTripsByOwner(result -> {
            if (result.data != null) {
                var currentDate = LocalDate.now();
                var list = result.data.stream().filter(v -> currentDate.isAfter(DateUtil.asLocalDate(v.getDate()))).collect(Collectors.toList());
                resulLiveData.postValue(list);
            }
        });
        return resulLiveData;
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