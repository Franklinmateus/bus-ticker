package com.bt.busticket.data;

import android.util.Log;
import com.bt.busticket.model.Account;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.ErrorType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class AccountRepository {

    FirebaseFirestore mFirestore;
    FirebaseAuth mFirebaseAuth;

    private final Executor executor;

    public AccountRepository(Executor executor) {
        this.executor = executor;
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void getAccountData(final ResultCallback<Account, ErrorType> callback) {
        executor.execute(() -> {
            try {
                var firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    callback.onComplete(new DataOrError<>(null, ErrorType.UNAUTHORIZED));
                    return;
                }
                String userUid = firebaseUser.getUid();
                mFirestore.collection("users").document(userUid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            var acc = document.toObject(Account.class);
                            callback.onComplete(new DataOrError<>(acc, null));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            callback.onComplete(new DataOrError<>(null, ErrorType.ACCOUNT_NOT_FOUND));
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        callback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            } catch (Exception e) {
                callback.onComplete(new DataOrError<>(null, ErrorType.GENERIC_ERROR));
            }
        });
    }

    public void changeUserType(String type, final ResultCallback<Boolean, ErrorType> callback) {
        executor.execute(() -> {
            var firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                callback.onComplete(new DataOrError<>(false, ErrorType.UNAUTHORIZED));
                return;
            }
            String userUid = firebaseUser.getUid();
            mFirestore.collection("users").document(userUid).update(Map.of("userType", type)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onComplete(new DataOrError<>(true, null));
                } else {
                    callback.onComplete(new DataOrError<>(false, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }

    public void updateAccountDetails(String name, String email, final ResultCallback<Boolean, ErrorType> callback) {
        executor.execute(() -> {
            var firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                callback.onComplete(new DataOrError<>(false, ErrorType.UNAUTHORIZED));
                return;
            }
            firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (email.equals(firebaseUser.getEmail())) {
                        callback.onComplete(new DataOrError<>(true, null));
                        return;
                    }
                    firebaseUser.verifyBeforeUpdateEmail(email).addOnCompleteListener(taskResult -> {
                        if (taskResult.isSuccessful()) {
                            callback.onComplete(new DataOrError<>(true, null));
                        } else {
                            callback.onComplete(new DataOrError<>(false, ErrorType.EMAIL_UPDATE_ERROR));
                        }
                    });
                } else {
                    callback.onComplete(new DataOrError<>(false, ErrorType.GENERIC_ERROR));
                }
            });
        });
    }

}
