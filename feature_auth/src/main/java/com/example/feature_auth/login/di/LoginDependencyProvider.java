package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.application.port.in.UseCaseRegistry;
import com.example.core.token.TokenManager;

public interface LoginDependencyProvider {
    @NonNull
    UseCaseRegistry getUseCaseRegistry();

    @NonNull
    TokenManager getTokenManager();
}
