package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.application.wrapper.UserSession;
import com.example.domain.common.value.AuthProvider;
import com.example.domain.user.entity.User;

/**
 * Stores the authenticated user's session-scoped data for the lifetime of the app process.
 */
public interface UserSessionStore {

    /** Returns the currently cached session or {@code null} when no session is active. */
    @Nullable
    UserSession getCurrentSession();

    /** Returns the currently cached user or {@code null} when no session is active. */
    @Nullable
    default User getCurrentUser() {
        UserSession current = getCurrentSession();
        return current != null ? current.getUser() : null;
    }

    /** @return {@code true} when a user session is available. */
    default boolean hasSession() {
        return getCurrentSession() != null;
    }

    /** Updates the cached session representation. */
    void update(@NonNull UserSession session);

    /** Updates only the cached user representation while preserving the provider state. */
    default void updateUser(@NonNull User user) {
        UserSession current = getCurrentSession();
        AuthProvider provider = current != null ? current.getProvider() : AuthProvider.GUEST;
        update(UserSession.of(user, provider));
    }

    /** Updates only the cached provider state while preserving the current user. */
    default void updateProvider(@NonNull AuthProvider provider) {
        UserSession current = getCurrentSession();
        if (current != null) {
            update(current.withProvider(provider));
        }
    }

    /**
     * Exposes the current session as a lifecycle-aware stream.
     */
    @NonNull
    LiveData<UserSession> getSessionStream();

    /**
     * Exposes the current user as a lifecycle-aware stream.
     */
    @NonNull
    LiveData<User> getUserStream();

    /** Clears any stored session information. */
    void clear();
}
