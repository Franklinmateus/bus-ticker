package com.cleios.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.auth.AuthManager;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.model.Account;
import com.google.firebase.firestore.FirebaseFirestore;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class HomeViewModel extends ViewModel {
    MutableLiveData<Account> account;
    FirebaseFirestore mFirestore;
    AuthRepository authRepository;

    public HomeViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void signOut() {
        AuthManager.getInstance().signOut();
    }


    public static final ViewModelInitializer<HomeViewModel> initializer = new ViewModelInitializer<>(
            HomeViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new HomeViewModel(app.appModule.authRepository);
            }
    );
}