package com.cleios.busticket.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentLoginBinding;
import com.cleios.busticket.model.AuthErrorType;
import com.cleios.busticket.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding mBinding;

    private FirebaseAuth mAuth;
    private LoginViewModel loginViewModel;

    private static final String TAG = "GoogleFragment";
    private SignInClient signInClient;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> handleSignInResult(result.getData())
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(LoginViewModel.initializer)
        ).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProgressBar(mBinding.progressBar);

        mBinding.emailSignInButton.setOnClickListener(v -> {
            String email = mBinding.fieldEmail.getEditText().getText().toString();
            String password = mBinding.fieldPassword.getEditText().getText().toString();
            signIn(email, password);
        });
        mBinding.emailCreateAccountButton.setOnClickListener(v -> navigateToRegister());
        mBinding.signInWithGoogleBtn.setOnClickListener(v -> signInWithGoogle());

        signInClient = Identity.getSignInClient(requireContext());

        mAuth = FirebaseAuth.getInstance();
    }

    private void handleSignInResult(Intent data) {
        try {
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            Log.d(TAG, "firebaseAuthWithGoogle:" + credential.getId());
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            hideProgressBar();
            if (!e.getStatus().isCanceled()){
                Toast.makeText(getContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgressBar();

        loginViewModel.signWithGoogle(idToken).observe(getViewLifecycleOwner(), result -> {
            hideProgressBar();
            if (result.data) {
                navigateToAccountRouter();
            } else if (result.error == AuthErrorType.INVALID_CREDENTIALS) {
                Snackbar.make(mBinding.mainLayout, R.string.wrong_email_or_password, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mBinding.mainLayout, R.string.some_error_has_occurred, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.google_server_client_id))
                .build();

        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(pendingIntent -> launchSignIn(pendingIntent))
                .addOnFailureListener(e -> Log.e(TAG, "Google Sign-in failed", e));
    }

    private void launchSignIn(PendingIntent pendingIntent) {
        try {
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
            signInLauncher.launch(intentSenderRequest);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't start Sign In: " + e.getLocalizedMessage());
        }
    }

    private void navigateToRegister() {
        NavHostFragment.findNavController(LoginFragment.this).navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToAccountRouter();
        }
    }

    private void navigateToAccountRouter() {
        NavHostFragment.findNavController(LoginFragment.this).navigate(LoginFragmentDirections.actionLoginFragmentToAccountTypeRouterFragment());
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        loginViewModel.signWithEmailPassword(email, password).observe(getViewLifecycleOwner(), result -> {
            hideProgressBar();
            if (result.data) {
                navigateToAccountRouter();
            } else if (result.error == AuthErrorType.INVALID_CREDENTIALS) {
                Toast.makeText(getContext(), R.string.wrong_email_or_password, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.some_error_has_occurred, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mBinding.fieldEmail.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            mBinding.fieldEmail.setError(getString(com.cleios.busticket.R.string.required));
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

        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
