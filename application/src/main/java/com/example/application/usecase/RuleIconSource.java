package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Describes where a rule icon drawable can be loaded from.
 */
public final class RuleIconSource {

    public enum Type {
        NONE,
        ASSET,
        DRAWABLE_RESOURCE
    }

    @NonNull
    private final Type type;
    @Nullable
    private final String value;

    private RuleIconSource(@NonNull Type type, @Nullable String value) {
        this.type = Objects.requireNonNull(type, "type == null");
        this.value = value;
    }

    @NonNull
    public static RuleIconSource none() {
        return new RuleIconSource(Type.NONE, null);
    }

    @NonNull
    public static RuleIconSource asset(@NonNull String relativePath) {
        return new RuleIconSource(Type.ASSET, Objects.requireNonNull(relativePath, "relativePath == null"));
    }

    @NonNull
    public static RuleIconSource drawableResource(@NonNull String resourceName) {
        return new RuleIconSource(Type.DRAWABLE_RESOURCE, Objects.requireNonNull(resourceName, "resourceName == null"));
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public boolean isPresent() {
        return type != Type.NONE && value != null && !value.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return "RuleIconSource{"
                + "type=" + type
                + ", value='" + value + '\''
                + '}';
    }
}
