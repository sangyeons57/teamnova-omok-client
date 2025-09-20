package com.example.domain.user.value;

import java.util.Objects;

/**
 * Value object representing a refresh token issued during authentication.
 */
public final class RefreshToken {

    private final String value;

    public RefreshToken(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public String value() {
        return value;
    }
}
