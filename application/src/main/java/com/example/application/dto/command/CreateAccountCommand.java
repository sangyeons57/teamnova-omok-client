package com.example.application.dto.command;

import com.example.domain.common.value.AuthProvider;

import java.util.Objects;

/**
 * Command object carrying the necessary information to initiate account creation.
 */
public final class CreateAccountCommand {

    private final AuthProvider provider;
    private final String googleIdToken;

    private CreateAccountCommand(AuthProvider provider, String googleIdToken) {
        this.provider = Objects.requireNonNull(provider, "provider");
        this.googleIdToken = googleIdToken;
    }

    public static CreateAccountCommand forGuest() {
        return new CreateAccountCommand(AuthProvider.GUEST, null);
    }

    public static CreateAccountCommand forGoogle(String providerUserId) {
        return new CreateAccountCommand(AuthProvider.GOOGLE, Objects.requireNonNull(providerUserId, "providerUserId"));
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }
}
