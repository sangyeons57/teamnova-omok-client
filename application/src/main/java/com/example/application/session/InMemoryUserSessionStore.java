package com.example.application.session;

import androidx.annotation.Nullable;

import com.example.domain.user.entity.User;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Default in-memory implementation that lives for the process lifetime.
 */
public class InMemoryUserSessionStore implements UserSessionStore {

    private final AtomicReference<User> current = new AtomicReference<>();

    @Nullable
    @Override
    public User getCurrentUser() {
        return current.get();
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user == null");
        }
        current.set(user);
    }

    @Override
    public void clear() {
        current.set(null);
    }
}

