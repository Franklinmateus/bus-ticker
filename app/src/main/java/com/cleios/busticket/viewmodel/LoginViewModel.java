package com.cleios.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.cleios.busticket.BusTicketApplication;
import com.cleios.busticket.data.AuthRepository;
import com.cleios.busticket.model.AuthErrorType;
import com.cleios.busticket.model.DataOrError;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class LoginViewModel extends ViewModel {
    AuthRepository authRepository;

    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public MutableLiveData<DataOrError<Boolean, AuthErrorType>> signWithEmailPassword(String email, String password) {
        var result = new MutableLiveData<DataOrError<Boolean, AuthErrorType>>();
        authRepository.signWithEmailPassword(email, password, result::postValue);
        return result;
    }

    public MutableLiveData<DataOrError<Boolean, AuthErrorType>> signWithGoogle(String idToken) {
        var result = new MutableLiveData<DataOrError<Boolean, AuthErrorType>>();
        authRepository.signWithGoogle(idToken, result::postValue);
        return result;
    }

    public static final ViewModelInitializer<LoginViewModel> initializer = new ViewModelInitializer<>(
            LoginViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new LoginViewModel(app.appModule.authRepository);
            }
    );
}