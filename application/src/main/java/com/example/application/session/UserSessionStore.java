package com.example.application.session;

import androidx.annotation.Nullable;

import com.example.domain.user.entity.User;

/**
 * Stores the authenticated user's session-scoped data for the lifetime of the app process.
 */
public interface UserSessionStore {

    /** Returns the currently cached user or {@code null} when no session is active. */
    @Nullable
    User getCurrentUser();

    /** @return {@code true} when a user session is available. */
    default boolean hasSession() {
        return getCurrentUser() != null;
    }

    /** Updates the cached user representation. */
    void update(User user);

    /** Clears any stored session information. */
    void clear();
}

