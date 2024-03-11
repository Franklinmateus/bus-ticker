package com.bt.busticket.ui.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.bt.busticket.data.OnCreateFileCallback;

import java.io.File;
import java.util.List;

public class PhotoPickerHandler {
    Activity activity;
    ActivityResultLauncher<Intent> launcher;
    Fragment fragmentOwner;
    private boolean isCameraPermissionGranted = false;
    private OnCreateFileCallback<Uri> onCreateFileCallback;
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    public PhotoPickerHandler(Activity activity, ActivityResultLauncher<Intent> launcher, Fragment fragmentOwner, OnCreateFileCallback<Uri> onCreateFileCallback) {
        this.activity = activity;
        this.launcher = launcher;
        this.fragmentOwner = fragmentOwner;
        this.onCreateFileCallback = onCreateFileCallback;
        requestPermissionLauncher = this.fragmentOwner.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), result -> {
                    if (result) {
                        selectImage();
                    }
                }
        );
    }

    public void selectImage() {
        getCameraPermission();
        if (!isCameraPermissionGranted) {
            return;
        }
        var file = createImageFile();
        if (file == null) return;

        var cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var authority = activity.getPackageName() + ".provider";
        var photoURI = FileProvider.getUriForFile(
                activity,
                authority,
                file
        );
        onCreateFileCallback.OnCreateFile(photoURI);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        var galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        var chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, List.of(cameraIntent).toArray(new Parcelable[]{}));

        if (chooserIntent.resolveActivity(activity.getPackageManager()) != null) {
            launcher.launch(chooserIntent);
        }

    }

    private File createImageFile() {
        var storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file;

        try {
            file = File.createTempFile("JPEG_FROM_CAM_PROFILE_PIC", ".jpg", storageDir);
        } catch (Exception exception) {
            return null;
        }
        return file;
    }


    private void getCameraPermission() {
        var permission = android.Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission);
        } else {
            isCameraPermissionGranted = true;
        }
    }
}
