package com.example.application.dto.command;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class LinkGoogleAccountCommand {

    private final String providerIdToken;

    private LinkGoogleAccountCommand(@NonNull String providerIdToken) {
        this.providerIdToken = Objects.requireNonNull(providerIdToken, "providerIdToken");
    }

    @NonNull
    public static LinkGoogleAccountCommand of(@NonNull String providerIdToken) {
        return new LinkGoogleAccountCommand(providerIdToken);
    }

    @NonNull
    public String providerIdToken() {
        return providerIdToken;
    }
}
