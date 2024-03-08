package com.cleios.busticket.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.DialogUserDetailBinding;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.SharedViewModel;
import com.cleios.busticket.viewmodel.UserDetailsViewModel;
import org.jetbrains.annotations.NotNull;

public class UserDetailDialogFragment extends DialogFragment {
    private UserDetailsViewModel mViewModel;
    private SharedViewModel sharedViewModel;
    private DialogUserDetailBinding binding;
    private CustomLoadingDialog loadingView;

    public UserDetailDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(UserDetailsViewModel.initializer)
        ).get(UserDetailsViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogUserDetailBinding.inflate(inflater, container, false);
        loadingView = new CustomLoadingDialog(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var user = mViewModel.getUser();
        binding.fieldName.getEditText().setText(user.getDisplayName());
        binding.fieldEmail.getEditText().setText(user.getEmail());

        binding.btnSave.setOnClickListener(v -> {
            updateAccount();
        });

        binding.btnEdit.setOnClickListener(v -> {
            enableFieldsAndSaveButton();
        });

        loadImageFromUri(mViewModel.getProfilePicture(), binding.profilePicture);
    }

    private void updateAccount() {
        if (!validateForm()) {
            return;
        }
        var email = binding.fieldEmail.getEditText().getText().toString();
        var name = binding.fieldName.getEditText().getText().toString();

        loadingView.show();
        mViewModel.updateDetails(name, email).observe(getViewLifecycleOwner(), result -> {
            loadingView.dismiss();
            if (result.data) {
                var user = mViewModel.getUser();
                assert user != null;
                if (!user.getEmail().equals(email)) {
                    Toast.makeText(requireContext(), R.string.verification_email_sent, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    sharedViewModel.setNewUsername(name);
                }
            } else if (result.error == ErrorType.EMAIL_UPDATE_ERROR){
                Toast.makeText(requireContext(), getString(R.string.error_updating_email), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
            }

            disableFieldsAndSaveButton();
        });
    }

    public void disableFieldsAndSaveButton() {
        binding.fieldEmail.setEnabled(false);
        binding.fieldName.setEnabled(false);
        binding.btnSave.setVisibility(View.GONE);
        binding.btnEdit.setVisibility(View.VISIBLE);
    }

    public void enableFieldsAndSaveButton() {
        binding.fieldEmail.setEnabled(true);
        binding.fieldName.setEnabled(true);
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnEdit.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadImageFromUri(Uri uri, ImageView view) {
        if (uri != null) {
            Glide.with(requireContext())
                    .load(uri).circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .into(view);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        var emailEdt = binding.fieldEmail.getEditText();
        if (emailEdt == null || TextUtils.isEmpty(emailEdt.getText().toString())) {
            binding.fieldEmail.setError(getString(com.cleios.busticket.R.string.required));
            valid = false;
        } else {
            binding.fieldEmail.setError(null);
        }
        return valid;
    }
}
