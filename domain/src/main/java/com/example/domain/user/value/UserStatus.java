package com.example.domain.user.value;

import java.util.Objects;

public final class UserStatus {
    public static final UserStatus EMPTY = new UserStatus("===EMPTY===");
    private final String value;
    private UserStatus(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }
    public static UserStatus of(String value) {
        Objects.requireNonNull(value, "value");
        return new UserStatus(value);
    }
    public String getValue() {
        return value;
    }
}
