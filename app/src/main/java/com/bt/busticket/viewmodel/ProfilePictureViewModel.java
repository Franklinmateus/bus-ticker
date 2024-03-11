package com.bt.busticket.viewmodel;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.data.FirebaseStorageService;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;

import static android.content.ContentValues.TAG;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class ProfilePictureViewModel extends ViewModel {
    private final FirebaseStorageService firebaseStorageService;

    public ProfilePictureViewModel(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    public static final ViewModelInitializer<ProfilePictureViewModel> initializer = new ViewModelInitializer<>(
            ProfilePictureViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new ProfilePictureViewModel(app.appModule.firebaseStorageService);
            }
    );

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

}