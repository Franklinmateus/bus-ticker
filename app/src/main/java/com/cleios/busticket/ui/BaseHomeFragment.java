package com.cleios.busticket.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.cleios.busticket.R;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.ui.helper.PhotoPickerHandler;
import org.jetbrains.annotations.NotNull;

public abstract class BaseHomeFragment extends Fragment {
    private PhotoPickerHandler photoPickerHandler;
    private Uri imageUri;

    public CustomLoadingDialog loadingView; // should be instantiated in onCreateView
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var data = result.getData();
                    if (data != null && data.getData() != null) {  //set image when it is from gallery
                        handlePhotoPickResult(data.getData());
                    } else if (imageUri != null) {   //set image when it is from camera
                        handlePhotoPickResult(imageUri);
                    }
                }
            });

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoPickerHandler = new PhotoPickerHandler(requireActivity(), imagePickerLauncher, this, fileUri -> imageUri = fileUri);
    }

    public void changePicture() {
        photoPickerHandler.selectImage();
    }

    // this method should be overridden
    public abstract void handlePhotoPickResult(Uri uri) ;

    public void loadImageFromUri(Uri uri, ImageView view) {
        if (uri != null) {
            Glide.with(requireContext())
                    .load(uri).circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .into(view);
        }
    }
}