package com.example.feature_auth.login.presentation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.core.token.TokenManager;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.usecase.CreateAccountUseCase;

import java.util.concurrent.RejectedExecutionException;

/**
 * ViewModel orchestrating login related interactions while preserving the MVVM boundaries.
 */
public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private static final String DEFAULT_GOOGLE_PROVIDER_USER_ID = "\uAD6C\uAE00 \uC2DD\uBCC4\uC790";


    private final MutableLiveData<LoginAction> loginAction = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // This constructor will be used by the ViewModelFactory
    public LoginViewModel() {

    }

    public LiveData<LoginAction> getLoginAction() {
        return loginAction;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void onGuestLoginClicked() {
        executeCreateAccount(CreateAccountUseCase.Params.forGuest(), LoginAction.GUEST);
    }

    public void onGoogleLoginClicked() {
        executeCreateAccount(CreateAccountUseCase.Params.forGoogle(DEFAULT_GOOGLE_PROVIDER_USER_ID), LoginAction.GOOGLE);
    }

    public void onActionHandled() {
        loginAction.setValue(null);
    }

    public void onErrorShown() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }

    private void executeCreateAccount(CreateAccountUseCase.Params params, LoginAction action) {
        try {
            executorService.execute(() -> {
                try {
                    GuestSignupResult result = createAccountUseCase.execute(params);
                    Log.d(TAG, "executeCreateAccount result: " + result.toString());

                    if (result.isSuccess()) {
                        TokenManager.getInstance().saveTokens(result.getAccessToken(), result.getRefreshToken());
                        loginAction.postValue(action);
                    } else {
                        errorMessage.postValue(result.getErrorMessage());
                    }

                } catch (Exception exception) {
                    Log.e(TAG, "executeCreateAccount failed", exception);
                    errorMessage.postValue(resolveErrorMessage(exception));
                }
            });
        } catch (RejectedExecutionException exception) {
            Log.e(TAG, "executeCreateAccount rejected", exception);
            errorMessage.postValue("Request rejected. Please try again later.");
        }
    }

    private String resolveErrorMessage(Exception exception) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty())
                ? "Failed to complete guest sign-up"
                : message;
    }
}
