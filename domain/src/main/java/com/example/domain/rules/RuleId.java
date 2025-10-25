package com.example.domain.rules;

import androidx.annotation.NonNull;

/**
 * Value object representing a unique rule identifier.
 */
public final class RuleId {

    private final int value;

    private RuleId(int value) {
        this.value = value;
    }

    @NonNull
    public static RuleId of(int value) {
        return new RuleId(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RuleId)) {
            return false;
        }
        RuleId other = (RuleId) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return "RuleId{" + value + '}';
    }
}
