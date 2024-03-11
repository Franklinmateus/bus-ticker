package com.bt.busticket.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.bt.busticket.BusTicketApplication;
import com.bt.busticket.data.AccountRepository;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class AccountTypeRouterViewModel extends ViewModel {
    private final AccountRepository accountRepository;

    public AccountTypeRouterViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public MutableLiveData<DataOrError<Boolean, ErrorType>> setUserType(String type) {
        var result = new MutableLiveData<DataOrError<Boolean, ErrorType>>();
        accountRepository.changeUserType(type, result::postValue);
        return result;
    }

    public static final ViewModelInitializer<AccountTypeRouterViewModel> initializer = new ViewModelInitializer<>(
            AccountTypeRouterViewModel.class,
            creationExtras -> {
                BusTicketApplication app = (BusTicketApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new AccountTypeRouterViewModel(app.appModule.accountRepository);
            }
    );
}