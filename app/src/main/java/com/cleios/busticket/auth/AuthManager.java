package com.cleios.busticket.auth;

import android.util.Log;
import com.cleios.busticket.model.AuthErrorType;
import com.cleios.busticket.model.DataOrError;
import com.cleios.busticket.data.ResultCallback;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AuthManager {

    private final FirebaseAuth mAuth;
    private static volatile AuthManager authManagerInstance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void signOut() {
        mAuth.signOut();
    }

    private AuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static AuthManager getInstance() {
        AuthManager instance = authManagerInstance;
        if (instance != null) {
            return instance;
        }
        synchronized (AuthManager.class) {
            if (authManagerInstance == null) {
                authManagerInstance = new AuthManager();
            }
            return authManagerInstance;
        }
    }

    public void signWithGoogle(String idToken, final ResultCallback<Boolean, AuthErrorType> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            var info = task.getResult().getAdditionalUserInfo();
                            if (info != null && info.isNewUser()) {
                                var name = user.getDisplayName();
                                var uid = user.getUid();
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("uid", uid);
                                newUser.put("name", name);

                                db.collection("users").document(uid).set(newUser)
                                        .addOnSuccessListener(documentReference -> callback.onComplete(new DataOrError<>(true, null)))
                                        .addOnFailureListener(e -> {
                                                    callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                        );
                            } else {
                                callback.onComplete(new DataOrError<>(true, null));
                            }
                        } else {
                            callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                        }
                    } else {
                        var ex = task.getException();
                        if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                            callback.onComplete(new DataOrError<>(false, AuthErrorType.INVALID_CREDENTIALS));
                        } else {
                            callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                        }
                    }
                });
    }

    public void signInWithEmailPassword(String email, String password, final ResultCallback<Boolean, AuthErrorType> callback) {
        AuthCredential credential = EmailAuthProvider.getCredential(email.trim(), password);

        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    callback.onComplete(new DataOrError<>(true, null));
                } else {
                    callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                }
            } else {
                var ex = task.getException();
                if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                    callback.onComplete(new DataOrError<>(false, AuthErrorType.INVALID_CREDENTIALS));
                } else {
                    callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                }
            }
        });
    }

    public void createAccountWithEmailPassword(String email, String password, final ResultCallback<Boolean, AuthErrorType> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        var user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification();
                            var uid = user.getUid();
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("uid", uid);

                            db.collection("users").document(uid).set(newUser)
                                    .addOnSuccessListener(documentReference -> callback.onComplete(new DataOrError<>(true, null)))
                                    .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error adding document", e);
                                                callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                                            }
                                    );
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            callback.onComplete(new DataOrError<>(false, AuthErrorType.ACCOUNT_ALREADY_EXISTS));
                        } else {
                            callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
                        }
                    }
                });
    }
}
