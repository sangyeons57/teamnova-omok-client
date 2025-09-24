package com.example.domain.user.value;

import java.util.Objects;

/**
 * Value object representing a refresh token issued during authentication.
 */
public final class RefreshToken {

    public static final RefreshToken EMPTY = new RefreshToken("===EMPTY===");

    private final String value;

    private RefreshToken(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RefreshToken of(String value) {
        Objects.requireNonNull(value, "value");
        return new RefreshToken(value);
    }

    public String getValue() {
        return value;
    }
}
