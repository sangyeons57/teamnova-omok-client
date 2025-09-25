package com.example.domain.user.value;

import java.util.Objects;

public final class UserRank {
    public static final UserRank EMPTY = new UserRank(0);

    private final int value;

    private UserRank(int value) {
        this.value = value;
    }

    public static UserRank of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("rank must be positive");
        }
        return new UserRank(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRank that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserRank{" + "value=" + value + '}';
    }
}
