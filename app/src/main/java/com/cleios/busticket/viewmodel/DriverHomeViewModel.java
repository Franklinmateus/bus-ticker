package com.cleios.busticket.viewmodel;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.R;
import com.cleios.busticket.auth.AuthManager;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.data.FirebaseStorageService;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.usecase.TripFinder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static android.content.ContentValues.TAG;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class DriverHomeViewModel extends ViewModel {
    FirebaseFirestore mFirestore;
    AuthRepository authRepository;
    private FirebaseStorageService firebaseStorageService;
    public TripFinder tripFinder;
    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public DriverHomeViewModel(AuthRepository authRepository, TripFinder tripFinder, FirebaseStorageService firebaseStorageService) {
        this.authRepository = authRepository;
        this.tripFinder = tripFinder;
        this.firebaseStorageService = firebaseStorageService;
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
                return new DriverHomeViewModel(app.appModule.authRepository, app.appModule.tripFinder, app.appModule.firebaseStorageService);
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

    public MutableLiveData<DataOrError<Uri, ErrorType>> saveProfileImage(Uri uri) {
        MutableLiveData<DataOrError<Uri, ErrorType>> resultLiveData = new MutableLiveData<>();
        firebaseStorageService.uploadImage(uri, result -> {
            if (result.data != null) {
                saveProfilePictureUrl(result.data, resultLiveData);
            } else {
                resultLiveData.postValue(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
            }
        });
        return resultLiveData;
    }

    private void saveProfilePictureUrl(StorageReference data, MutableLiveData<DataOrError<Uri, ErrorType>> resultLiveData) {
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        firebaseStorageService.getDownloadUrlFromReference(data, result -> {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(result.data)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(taskResult -> {
                        if (taskResult.isSuccessful()) {
                            resultLiveData.postValue(new DataOrError<>(result.data, ErrorType.GENERIC_ERROR));
                            Log.d(TAG, "User profile updated.");
                        } else {
                            resultLiveData.postValue(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
                        }
                    });
        });
    }

    public Uri getProfilePicture() {
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;
        return user.getPhotoUrl();
    }
}