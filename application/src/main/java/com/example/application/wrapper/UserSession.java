package com.example.application.wrapper;

import androidx.annotation.NonNull;

import com.example.domain.common.value.AuthProvider;
import com.example.domain.user.entity.User;

import java.util.Objects;

/**
 * Value object storing the active user data alongside the authentication provider state.
 */
public final class UserSession {

    private final User user;
    private final AuthProvider provider;

    private UserSession(@NonNull User user, @NonNull AuthProvider provider) {
        this.user = Objects.requireNonNull(user, "user");
        this.provider = Objects.requireNonNull(provider, "provider");
    }

    @NonNull
    public static UserSession of(@NonNull User user, @NonNull AuthProvider provider) {
        return new UserSession(user, provider);
    }

    @NonNull
    public User getUser() {
        return user;
    }

    @NonNull
    public AuthProvider getProvider() {
        return provider;
    }

    public boolean isGoogleLinked() {
        return provider == AuthProvider.GOOGLE;
    }

    @NonNull
    public UserSession withUser(@NonNull User updatedUser) {
        return new UserSession(Objects.requireNonNull(updatedUser, "updatedUser"), provider);
    }

    @NonNull
    public UserSession withProvider(@NonNull AuthProvider updatedProvider) {
        return new UserSession(user, Objects.requireNonNull(updatedProvider, "updatedProvider"));
    }
}
