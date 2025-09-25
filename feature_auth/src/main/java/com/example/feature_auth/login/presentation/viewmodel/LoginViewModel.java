package com.example.feature_auth.login.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.dto.command.CreateAccountCommand;
import com.example.application.dto.response.LoginResponse;
import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.application.usecase.LoginUseCase;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core.token.TokenStore;
import com.example.domain.common.value.LoginAction;
import com.example.domain.user.entity.Identity;
import com.example.domain.user.entity.User;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * ViewModel orchestrating login related interactions while preserving the MVVM boundaries.
 */
public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private final MutableLiveData<LoginAction> loginAction = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ExecutorService executorService;
    private final CreateAccountUseCase createAccountUseCase;
    private final TokenStore tokenManager;

    public LoginViewModel(@NonNull CreateAccountUseCase createAccountUseCase,
                          @NonNull LoginUseCase loginUseCase,
                          @NonNull TokenStore tokenManager,
                          @NonNull ExecutorService executorservice,
                          @NonNull FragmentNavigationHost<AppNavigationKey> host) {
        this.createAccountUseCase = Objects.requireNonNull(createAccountUseCase, "createAccountUseCase");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.executorService = Objects.requireNonNull(executorservice, "executorService");

        loginUseCase.executeAsync(UseCase.None.INSTANCE, executorService).whenComplete((result, throwable) -> {
            Log.d("LoginViewModel", "loginUseCase.executeAsync: " + result);
            if(result instanceof UResult.Ok<LoginResponse> data) {
                Log.d("LoginViewModel", "loginUseCase.executeAsync: " + data.value().toString());
                host.clearBackStack();
                host.navigateTo(AppNavigationKey.HOME, false);
            } else if (result instanceof UResult.Err<LoginResponse> err) {
                Log.d("LoginViewModel", "loginUseCase.executeAsync: " + err.message());
                handleFailure(err.message());
            }
        });

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

    public void onActionHandled() {
        loginAction.setValue(null);
    }

    public void onErrorShown() {
        errorMessage.setValue(null);
    }

    public void onGoogleCredentialReceived(@NonNull String providerUserId) {
        executeCreateAccount(CreateAccountCommand.forGoogle(providerUserId), LoginAction.GOOGLE);
    }

    public void onGoogleSignInFailed(String message) {
        handleFailure(message);
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }

    private void executeCreateAccount(CreateAccountCommand command, LoginAction action) {
        createAccountUseCase.executeAsync(command, executorService).thenAccept(result -> {
            Log.d(TAG, "executeCreateAccount: " + result);
            if (result instanceof UResult.Ok<User> ok) {
                handleSuccess(ok.value().getIdentity(), action);
            } else if (result instanceof UResult.Err<User> err) {
                handleFailure(err.message());
            }
        });
    }

    private void handleSuccess(Identity response, LoginAction action) {
        tokenManager.saveTokens(response.getAccessToken().getValue(), response.getRefreshToken().getValue());
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
