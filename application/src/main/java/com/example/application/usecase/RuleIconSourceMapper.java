package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Maps persisted rule icon paths into {@link RuleIconSource} descriptors.
 */
public final class RuleIconSourceMapper {

    private static final String ASSET_PREFIX = "asset://";
    private static final String DRAWABLE_PREFIX = "@drawable/";
    private static final String DRAWABLE_RESOURCE_PREFIX = "res://drawable/";

    private RuleIconSourceMapper() {
        // Utility class.
    }

    /**
     * Converts a raw icon path into a {@link RuleIconSource}.
     *
     * @param rawPath path persisted with the rule, may be {@code null} or blank.
     * @return resolved source descriptor, never {@code null}.
     */
    @NonNull
    public static RuleIconSource fromPath(@Nullable String rawPath) {
        if (rawPath == null) {
            return RuleIconSource.none();
        }
        String trimmed = rawPath.trim();
        if (trimmed.isEmpty()) {
            return RuleIconSource.none();
        }
        if (trimmed.startsWith(ASSET_PREFIX)) {
            return RuleIconSource.asset(trimmed.substring(ASSET_PREFIX.length()));
        }
        if (trimmed.startsWith(DRAWABLE_PREFIX)) {
            return RuleIconSource.drawableResource(trimmed.substring(DRAWABLE_PREFIX.length()));
        }
        if (trimmed.startsWith(DRAWABLE_RESOURCE_PREFIX)) {
            return RuleIconSource.drawableResource(trimmed.substring(DRAWABLE_RESOURCE_PREFIX.length()));
        }
        String lower = trimmed.toLowerCase(java.util.Locale.US);
        if (trimmed.contains("/") || lower.endsWith(".png")
                || lower.endsWith(".webp")
                || lower.endsWith(".jpg")) {
            return RuleIconSource.asset(trimmed);
        }
        return RuleIconSource.drawableResource(trimmed);
    }
}
