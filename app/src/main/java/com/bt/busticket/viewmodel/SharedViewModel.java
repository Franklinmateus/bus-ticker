package com.bt.busticket.viewmodel;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.data.AccountRepository;
import com.bt.busticket.model.Account;
import com.bt.busticket.model.ErrorType;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class SharedViewModel extends ViewModel {
    private final AccountRepository accountRepository;
    private MutableLiveData<Account> accountMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ErrorType> error = new MutableLiveData<>();

    public SharedViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public LiveData<Account> getAccount() {
        return accountMutableLiveData;
    }

    public LiveData<ErrorType> getError() {
        return error;
    }

    public void loadAccount() {
        accountRepository.getAccountData(result -> {
            if (result.data != null) {
                var acc = result.data;
                var user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    acc.setEmail(user.getEmail());
                    acc.setName(user.getDisplayName());
                    acc.setProfilePhotoUri(user.getPhotoUrl());
                }
                accountMutableLiveData.postValue(acc);
            } else {
                error.postValue(result.error);
            }
        });
    }

    public static final ViewModelInitializer<SharedViewModel> initializer = new ViewModelInitializer<>(
            SharedViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new SharedViewModel(app.appModule.accountRepository);
            }
    );

    public void setNewProfilePicture(Uri uri) {
        var acc = accountMutableLiveData.getValue();
        if (acc != null) {
            acc.setProfilePhotoUri(uri);
            accountMutableLiveData.postValue(acc);
        }
    }

    public void setNewUsername(String name) {
        var acc = accountMutableLiveData.getValue();
        if (acc != null) {
            acc.setName(name);
            accountMutableLiveData.postValue(acc);
        }
    }

    public void clear() {
        accountMutableLiveData.setValue(null);
    }
}