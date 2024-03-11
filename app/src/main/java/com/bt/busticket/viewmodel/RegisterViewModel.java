package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.R;
import com.bt.busticket.data.AuthRepository;
import com.bt.busticket.model.AuthErrorType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class RegisterViewModel extends ViewModel {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public AuthRepository authRepository;

    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void createAccount(String email, String password) {
        authRepository.createAccountWithEmailPassword(email, password, result -> {
            if (result.data) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    userLiveData.postValue(user);
                }
            } else if (result.error == AuthErrorType.ACCOUNT_ALREADY_EXISTS) {
                errorLiveData.postValue(R.string.account_already_exists);
            } else {
                errorLiveData.postValue(R.string.error_creating_account);
            }
        });
    }

    public static final ViewModelInitializer<RegisterViewModel> initializer = new ViewModelInitializer<>(
            RegisterViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new RegisterViewModel(app.appModule.authRepository);
            }
    );
}