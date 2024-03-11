package com.bt.busticket.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.bt.busticket.R;
import com.bt.busticket.ui.helper.CustomLoadingDialog;
import com.bt.busticket.ui.helper.PhotoPickerHandler;
import com.bt.busticket.viewmodel.ProfilePictureViewModel;
import com.bt.busticket.viewmodel.SharedViewModel;
import org.jetbrains.annotations.NotNull;

public class ChangeProfilePhotoDialogFragment extends DialogFragment {

    private PhotoPickerHandler photoPickerHandler;
    private SharedViewModel sharedViewModel;
    private Uri imageUri;
    private ProfilePictureViewModel mViewModel;
    public CustomLoadingDialog loadingView;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var data = result.getData();
                    if (data != null && data.getData() != null) {
                        handlePhotoPickResult(data.getData());
                    } else if (imageUri != null) {
                        handlePhotoPickResult(imageUri);
                    }
                } else {
                    dismiss();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadingView = new CustomLoadingDialog(requireContext());
        return inflater.inflate(R.layout.empty_layout, container);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ProfilePictureViewModel.initializer)
        ).get(ProfilePictureViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoPickerHandler = new PhotoPickerHandler(requireActivity(), imagePickerLauncher, this, fileUri -> imageUri = fileUri);

        changePicture();
    }

    public void changePicture() {
        photoPickerHandler.selectImage();
    }

    private void handlePhotoPickResult(Uri uri) {
        loadingView.show();
        mViewModel.saveProfileImage(uri).observe(getViewLifecycleOwner(), result -> {
            if (result.data != null) {
                loadingView.dismiss();
                sharedViewModel.setNewProfilePicture(uri);
                Toast.makeText(requireContext(), getString(R.string.profile_picture_changed), Toast.LENGTH_SHORT).show();
            } else {
                loadingView.dismiss();
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
