package com.example.domain.user.value;

import java.util.Objects;

public final class UserRole {
    public static final UserRole EMPTY = new UserRole("===EMPTY===");
    private final String value;
    private UserRole(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static UserRole of(String value) {
        Objects.requireNonNull(value, "value");
        return new UserRole(value);
    }

    public String getValue() {
        return value;
    }
}
