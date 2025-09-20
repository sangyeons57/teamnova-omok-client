package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.port.in.UseCaseRegistry;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.core.token.TokenManager;
import com.example.feature_auth.login.presentation.viewmodel.LoginViewModel;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final UseCaseRegistry useCaseRegistry;
    private final TokenManager tokenManager;
    private final ExecutorService executorService;

    private LoginViewModelFactory(@NonNull UseCaseRegistry useCaseRegistry,
                                  @NonNull TokenManager tokenManager,
                                  @NonNull ExecutorService executorService) {
        this.useCaseRegistry = Objects.requireNonNull(useCaseRegistry, "useCaseRegistry");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.executorService = Objects.requireNonNull(executorService, "executorService");
    }

    @NonNull
    public static LoginViewModelFactory create(@NonNull UseCaseRegistry registry,
                                               @NonNull TokenManager tokenManager) {
        return new LoginViewModelFactory(registry, tokenManager, Executors.newSingleThreadExecutor());
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            CreateAccountUseCase useCase = useCaseRegistry.get(CreateAccountUseCase.class);
            return (T) new LoginViewModel(useCase, tokenManager, executorService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
