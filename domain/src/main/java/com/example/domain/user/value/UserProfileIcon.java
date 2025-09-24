package com.example.domain.user.value;

import java.util.Objects;

public final class UserProfileIcon {
    public static final UserProfileIcon EMPTY = new UserProfileIcon("===EMPTY===");
    private final String value;
    private UserProfileIcon(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }
    public static UserProfileIcon of(String value) {
        Objects.requireNonNull(value, "value");
        return new UserProfileIcon(value);
    }
    public String getValue() {
        return value;
    }
}
