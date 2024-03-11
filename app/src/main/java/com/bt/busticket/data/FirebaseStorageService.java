package com.bt.busticket.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;

public class FirebaseStorageService {

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private final Context context;
    private final Executor executor;

    public FirebaseStorageService(Context context, Executor executor) {
        this.context = context;
        this.executor = executor;
    }

    public void uploadImage(Uri uri, ResultCallback<StorageReference, ErrorType> resultCallback) {
        executor.execute(() -> {
            var uploadTask = upload(uri);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    var taskSnapshot = task.getResult();
                    var ref = taskSnapshot.getStorage();
                    resultCallback.onComplete(new DataOrError<>(ref, null));
                } else {
                    resultCallback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }

    private UploadTask upload(Uri uri) {
        var accountUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        var imageRef = storageRef.child("accounts/" + accountUid + "/" + uri.getLastPathSegment());

        var imageBitmap = getBitmapFromUri(uri);

        var byteArray = getBytesFromBitmap(imageBitmap, 70);

        return imageRef.putBytes(byteArray);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bmp = null;
        try {

            Matrix mat = getMatrix(uri);
            var contentResolver = context.getContentResolver();
            var inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), mat, true);

                inputStream.close();
            }
        } catch (Exception ex) {
            Log.e("getBitmapFromUri", ex.getMessage());
        }
        return bmp;
    }

    private Matrix getMatrix(Uri uri) throws IOException {
        var contentResolver = context.getContentResolver();
        var inputStream = contentResolver.openInputStream(uri);
        var exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        int angle = 0;

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            angle = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
        }
        Matrix mat = new Matrix();
        mat.postRotate(angle);

        inputStream.close();
        return mat;
    }

    private static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                baos);
        return baos.toByteArray();
    }

    public void getDownloadUrlFromReference(StorageReference ref, ResultCallback<Uri, ErrorType> resultCallback) {
        executor.execute(() -> ref.getDownloadUrl().addOnSuccessListener(l -> resultCallback.onComplete(new DataOrError<>(l, null))));
    }
}
