package com.example.feature_auth.login.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.dto.command.CreateAccountCommand;
import com.example.application.port.in.UResult;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.core.token.TokenManager;
import com.example.domain.common.value.LoginAction;
import com.example.domain.identity.entity.Identity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
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

    public LoginViewModel(@NonNull CreateAccountUseCase createAccountUseCase,
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
        executeCreateAccount(CreateAccountCommand.forGuest(), LoginAction.GUEST);
    }

    public void onGoogleLoginClicked() {
        executeCreateAccount(CreateAccountCommand.forGoogle(DEFAULT_GOOGLE_PROVIDER_USER_ID), LoginAction.GOOGLE);
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

    private void executeCreateAccount(CreateAccountCommand command, LoginAction action) {
        createAccountUseCase.executeAsync(command, executorService).thenAccept(result -> {
            Log.d(TAG, "executeCreateAccount: " + result);
            if (result instanceof UResult.Ok<Identity> ok) {
                handleSuccess(ok.value(), action);
            } else if (result instanceof UResult.Err<Identity> err) {
                handleFailure(err.message());
            }
        });
    }

    private void handleSuccess(Identity response, LoginAction action) {
        tokenManager.saveTokens(response.getAccessToken().value(), response.getRefreshToken().value());
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
