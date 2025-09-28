package com.example.application.dto.command;

import com.example.domain.common.value.SignupAction;

import java.util.Objects;

/**
 * Command object carrying the necessary information to initiate account creation.
 */
public final class CreateAccountCommand {

    private final SignupAction provider;
    private final String googleIdToken;

    private CreateAccountCommand(SignupAction provider, String googleIdToken) {
        this.provider = Objects.requireNonNull(provider, "provider");
        this.googleIdToken = googleIdToken;
    }

    public static CreateAccountCommand forGuest() {
        return new CreateAccountCommand(SignupAction.GUEST, null);
    }

    public static CreateAccountCommand forGoogle(String providerUserId) {
        return new CreateAccountCommand(SignupAction.GOOGLE, Objects.requireNonNull(providerUserId, "providerUserId"));
    }

    public SignupAction getProvider() {
        return provider;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }
}
