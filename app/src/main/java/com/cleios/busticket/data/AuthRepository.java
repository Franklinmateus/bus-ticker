package com.cleios.busticket.data;

import com.cleios.busticket.auth.AuthManager;
import com.cleios.busticket.model.AuthErrorType;
import com.cleios.busticket.model.DataOrError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class AuthRepository {

    FirebaseAuth mFirebaseAuth;
    private final Executor executor;

    public AuthRepository(Executor executor) {
        this.executor = executor;
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void createAccountWithEmailPassword(String email, String password, final ResultCallback<Boolean, AuthErrorType> callback) {
        executor.execute(() -> {
            try {
                AuthManager.getInstance().createAccountWithEmailPassword(email, password, callback);
            } catch (Exception e) {
                callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
            }
        });
    }

    public void signWithEmailPassword(String email, String password, final ResultCallback<Boolean, AuthErrorType> callback) {
        executor.execute(() -> {
            try {
                AuthManager.getInstance().signInWithEmailPassword(email, password, callback);
            } catch (Exception e) {
                callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
            }
        });
    }

    public void signWithGoogle(String idToken, final ResultCallback<Boolean, AuthErrorType> callback) {
        executor.execute(() -> {
            try {
                AuthManager.getInstance().signWithGoogle(idToken, callback);
            } catch (Exception e) {
                callback.onComplete(new DataOrError<>(false, AuthErrorType.GENERIC_ERROR));
            }
        });
    }
}
