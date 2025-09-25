package com.example.domain.user.value;

import java.util.Objects;

public final class UserProfileIcon {
    public static final UserProfileIcon EMPTY = new UserProfileIcon(-9999);
    private final int value;
    private UserProfileIcon(int value) {
        this.value = Objects.requireNonNull(value, "value");
    }
    public static UserProfileIcon of(int value) {
        Objects.requireNonNull(value, "value");
        return new UserProfileIcon(value);
    }
    public int getValue() {
        return value;
    }
}
