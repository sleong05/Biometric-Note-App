package com.example.biometricnoteapp.services;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class LoginManager {

    private final FragmentActivity activity;
    private final Executor executor;
    private final BiometricPrompt.PromptInfo promptInfo;

    public interface AuthenticationCallback {
        void onSuccess();
    }

    public interface FailureCallback {
        void onFailure();
    }

    private AuthenticationCallback successCallback;
    private FailureCallback failureCallback;

    public LoginManager(FragmentActivity activity) {
        this.activity = activity;
        this.executor = ContextCompat.getMainExecutor(activity);

        this.promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Login")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")  // Required — was causing your crash
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setConfirmationRequired(false)
                .build();
    }

    public void setSuccessCallback(AuthenticationCallback callback) {
        this.successCallback = callback;
    }

    public void setFailureCallback(FailureCallback callback) {
        this.failureCallback = callback;
    }

    public void authenticate() {
        int canAuthenticate = BiometricManager.from(activity)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(activity,
                    "No enrolled fingerprint. Please enroll in settings.",
                    Toast.LENGTH_LONG).show();
            if (failureCallback != null) failureCallback.onFailure();
            return;
        }

        // Build BiometricPrompt here so it's always tied to the live activity
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (successCallback != null) successCallback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (failureCallback != null) failureCallback.onFailure();
                    }
                });

        biometricPrompt.authenticate(promptInfo);
    }
}