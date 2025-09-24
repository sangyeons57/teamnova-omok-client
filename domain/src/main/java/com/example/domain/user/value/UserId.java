package com.example.domain.user.value;

import java.util.Objects;

public final class UserId {
    public static final UserId EMPTY = new UserId("===EMPTY===");
    private final String value;
    private UserId(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }
    public static UserId of(String value) {
        Objects.requireNonNull(value, "value");
        return new UserId(value);
    }
    public String getValue() {
        return value;
    }
}
