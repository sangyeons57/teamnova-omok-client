package com.example.domain.rules;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Value object representing the short code for a rule.
 */
public final class RuleCode {

    @NonNull
    private final String value;

    private RuleCode(@NonNull String value) {
        this.value = value;
    }

    @NonNull
    public static RuleCode of(@NonNull String value) {
        return new RuleCode(Objects.requireNonNull(value, "value == null"));
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RuleCode)) {
            return false;
        }
        RuleCode other = (RuleCode) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "RuleCode{" + value + '}';
    }
}
