package com.bt.busticket.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bt.busticket.R;
import com.bt.busticket.databinding.FragmentRegisterBinding;
import com.bt.busticket.viewmodel.RegisterViewModel;

public class RegisterFragment extends BaseFragment {

    private FragmentRegisterBinding mBinding;

    private RegisterViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(RegisterViewModel.initializer)
        ).get(RegisterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProgressBar(mBinding.progressBar);

        mBinding.createAccountButton.setOnClickListener(v -> {
            String email = mBinding.fieldEmail.getEditText().getText().toString();
            String password = mBinding.fieldPassword.getEditText().getText().toString();
            createAccount(email, password);
        });

        mBinding.hasAccountButton.setOnClickListener(v -> {
            navigateToLogin();
        });

        mViewModel.userLiveData.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                hideProgressBar();
                navigateToAccountRouter();
            } else {
                //todo: show message
            }
        });

        mViewModel.errorLiveData.observe(getViewLifecycleOwner(), messageResourceId -> {
            hideProgressBar();
            Toast.makeText(getContext(), getString(messageResourceId),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        mViewModel.createAccount(email, password);
    }

    private void navigateToAccountRouter() {
        NavHostFragment.findNavController(RegisterFragment.this)
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToAccountTypeRouterFragment());
    }

    private void navigateToLogin() {
        NavHostFragment.findNavController(RegisterFragment.this)
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mBinding.fieldEmail.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            mBinding.fieldEmail.setError(getString(R.string.required));
            valid = false;
        } else {
            mBinding.fieldEmail.setError(null);
        }

        String password = mBinding.fieldPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            mBinding.fieldPassword.setError(getString(R.string.required));
            valid = false;
        } else {
            mBinding.fieldPassword.setError(null);
        }

        String confirmPassword = mBinding.fieldConfirmPassword.getEditText().getText().toString();
        if (!confirmPassword.equals(password)) {
            mBinding.fieldConfirmPassword.setError(getString(R.string.different_passwords));
            valid = false;
        } else {
            mBinding.fieldConfirmPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
