package com.bt.busticket.viewmodel;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.data.AccountRepository;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;
import com.bt.busticket.model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class UserDetailsViewModel extends ViewModel {
    FirebaseFirestore mFirestore;
    private final AccountRepository accountRepository;
    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Trip>> tripsLiveData = new MutableLiveData<>();

    public UserDetailsViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static final ViewModelInitializer<UserDetailsViewModel> initializer = new ViewModelInitializer<>(
            UserDetailsViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new UserDetailsViewModel(app.appModule.accountRepository);
            }
    );

    public Uri getProfilePicture() {
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;
        return user.getPhotoUrl();
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public MutableLiveData<DataOrError<Boolean, ErrorType>> updateDetails(String name, String email) {
        MutableLiveData<DataOrError<Boolean, ErrorType>> resultLiveData = new MutableLiveData<>();
        accountRepository.updateAccountDetails(name, email, resultLiveData::postValue);
        return resultLiveData;
    }
}