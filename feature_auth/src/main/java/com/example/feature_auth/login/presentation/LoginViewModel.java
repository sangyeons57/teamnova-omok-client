package com.example.feature_auth.login.presentation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.core.token.TokenManager;
import com.example.data.common.repository.DefaultLoginRepository;
import com.example.domain.domain.auth.model.GuestSignupResult;
import com.example.domain.domain.auth.model.LoginAction;
import com.example.domain.domain.auth.usecase.CreateAccountUseCase;
import com.example.domain.application.UResult;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * ViewModel orchestrating login related interactions while preserving the MVVM boundaries.
 */
public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private static final String DEFAULT_GOOGLE_PROVIDER_USER_ID = "\uAD6C\uAE00 \uC2DD\uBCC4\uC790";

    private final MutableLiveData<LoginAction> loginAction = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ExecutorService executorService;
    private final CreateAccountUseCase createAccountUseCase;
    private final TokenManager tokenManager;

    public LoginViewModel() {
        this(new CreateAccountUseCase(new DefaultLoginRepository()),
                TokenManager.getInstance(),
                Executors.newSingleThreadExecutor());
    }

    LoginViewModel(@NonNull CreateAccountUseCase createAccountUseCase,
                   @NonNull TokenManager tokenManager,
                   @NonNull ExecutorService executorService) {
        this.createAccountUseCase = Objects.requireNonNull(createAccountUseCase, "createAccountUseCase");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.executorService = Objects.requireNonNull(executorService, "executorService");
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
                    UResult<GuestSignupResult> result = createAccountUseCase.execute(params);
                    if (result instanceof UResult.Ok<GuestSignupResult> ok) {
                        handleSuccess(ok.value(), action);
                    } else if (result instanceof UResult.Err<GuestSignupResult> err) {
                        handleFailure(err.message());
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

    private void handleSuccess(GuestSignupResult result, LoginAction action) {
        tokenManager.saveTokens(result.getAccessToken(), result.getRefreshToken());
        loginAction.postValue(action);
    }

    private void handleFailure(String message) {
        if (message == null || message.trim().isEmpty()) {
            errorMessage.postValue("Failed to complete guest sign-up");
        } else {
            errorMessage.postValue(message);
        }
    }

    private String resolveErrorMessage(Exception exception) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty())
                ? "Failed to complete guest sign-up"
                : message;
    }
}
