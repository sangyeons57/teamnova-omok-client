package com.example.application.dto.command;

import com.example.domain.common.value.LoginAction;

import java.util.Objects;

/**
 * Command object carrying the necessary information to initiate account creation.
 */
public final class CreateAccountCommand {

    private final LoginAction provider;
    private final String googleIdToken;

    private CreateAccountCommand(LoginAction provider, String googleIdToken) {
        this.provider = Objects.requireNonNull(provider, "provider");
        this.googleIdToken = googleIdToken;
    }

    public static CreateAccountCommand forGuest() {
        return new CreateAccountCommand(LoginAction.GUEST, null);
    }

    public static CreateAccountCommand forGoogle(String providerUserId) {
        return new CreateAccountCommand(LoginAction.GOOGLE, Objects.requireNonNull(providerUserId, "providerUserId"));
    }

    public LoginAction getProvider() {
        return provider;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }
}
