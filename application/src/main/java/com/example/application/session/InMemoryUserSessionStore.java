package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Default in-memory implementation that lives for the process lifetime.
 */
public class InMemoryUserSessionStore implements UserSessionStore {

    private final AtomicReference<User> current = new AtomicReference<>();
    private final MutableLiveData<User> userStream = new MutableLiveData<>();

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
        User sanitized = UserFactory.withoutIdentity(user);
        current.set(sanitized);
        userStream.postValue(sanitized);
    }

    @NonNull
    @Override
    public LiveData<User> getUserStream() {
        User existing = current.get();
        if (existing != null && userStream.getValue() == null) {
            userStream.setValue(existing);
        }
        return userStream;
    }

    @Override
    public void clear() {
        current.set(null);
        userStream.postValue(null);
    }
}
