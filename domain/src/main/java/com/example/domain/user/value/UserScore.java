package com.example.domain.user.value;

public final class UserScore {
    public static final UserScore EMPTY = new UserScore(-9999);
    private final int value;
    private UserScore(int value) {
        this.value = value;
    }
    public static UserScore of(int value) {
        return new UserScore(value);
    }
    public int getValue() {
        return value;
    }
}
