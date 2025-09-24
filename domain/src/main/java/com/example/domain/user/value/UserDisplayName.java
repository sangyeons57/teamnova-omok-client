package com.example.domain.user.value;

import java.util.Objects;

public final class UserDisplayName {
    public static final UserDisplayName EMPTY = new UserDisplayName("===EMPTY===");

    private final String value;
    private UserDisplayName(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }
    public static UserDisplayName of(String value) {
        Objects.requireNonNull(value, "value");
        return new UserDisplayName(value);
    }
    public String getValue() {
        return value;
    }
}
