package com.cleios.busticket.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.cleios.busticket.R;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.databinding.FragmentAccountTypeRouterBinding;
import com.cleios.busticket.viewmodel.AccountTypeRouterViewModel;
import org.jetbrains.annotations.NotNull;

public class AccountTypeRouterFragment extends Fragment {
    private FragmentAccountTypeRouterBinding binding;
    private AccountTypeRouterViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AccountTypeRouterViewModel.initializer)).get(AccountTypeRouterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountTypeRouterBinding.inflate(inflater, container, false);

        mViewModel.getAccountData().observe(getViewLifecycleOwner(), result -> {
            if (result.data != null) {
                var account = result.data;
                if (account.getUserType() == null) {
                    binding.appNameLogo.setVisibility(View.GONE);
                    binding.accountTypeContainer.setVisibility(View.VISIBLE);
                } else if (account.getUserType().equals("passenger")) {
                } else if (account.getUserType().equals("driver")) {
                    NavHostFragment.findNavController(AccountTypeRouterFragment.this)
                            .navigate(AccountTypeRouterFragmentDirections.actionAccountTypeRouterFragmentToDriverHomeFragment());
                }
            } else {
                handleError(result.error);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userTypeRadioGroup.setOnCheckedChangeListener((RadioGroup rg, int id) -> binding.btnContinue.setEnabled(true));

        binding.btnContinue.setOnClickListener(view1 -> {
            var rbId = binding.userTypeRadioGroup.getCheckedRadioButtonId();
            if (rbId == binding.rbDriver.getId()) {
                setUserType("driver");
            } else {
                setUserType("passenger");
            }
        });
    }

    private void handleError(ErrorType errorType) {
        if (errorType == ErrorType.UNAUTHORIZED) {
            NavHostFragment.findNavController(this).navigate(AccountTypeRouterFragmentDirections.actionAccountTypeRouterFragmentToLoginFragment());
        } else if (errorType == ErrorType.GENERIC_ERROR) {
            Toast.makeText(requireContext(), "Algum erro ocorreu", Toast.LENGTH_SHORT).show();
        } else if (errorType == ErrorType.ACCOUNT_NOT_FOUND) {
            Toast.makeText(requireContext(), "Erro ao obter detalhes da conta", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserType(String type) {
        mViewModel.setUserType(type).observe(getViewLifecycleOwner(), result -> {
            if (result.data) {
                if (type.equals("driver")) {
                    NavHostFragment.findNavController(AccountTypeRouterFragment.this)
                            .navigate(AccountTypeRouterFragmentDirections.actionAccountTypeRouterFragmentToDriverHomeFragment());
                } else {
                    // go to passenger
                }
            } else if (result.error == ErrorType.UNAUTHORIZED) {
                NavHostFragment.findNavController(this).navigate(AccountTypeRouterFragmentDirections.actionAccountTypeRouterFragmentToLoginFragment());
            } else if (result.error == ErrorType.GENERIC_ERROR) {
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}