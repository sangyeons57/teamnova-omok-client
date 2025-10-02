package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.application.wrapper.UserSession;
import com.example.domain.common.value.AuthProvider;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Stores the authenticated user's session-scoped data for the lifetime of the app process.
 */
public class UserSessionStore {

    private final AtomicReference<UserSession> current = new AtomicReference<>();
    private final MutableLiveData<UserSession> sessionStream = new MutableLiveData<>();
    private final MutableLiveData<User> userStream = new MutableLiveData<>();

    @Nullable
    public UserSession getCurrentSession() {
        return current.get();
    }

    @Nullable
    public User getCurrentUser() {
        UserSession session = current.get();
        return session != null ? session.getUser() : null;
    }

    public void update(@NonNull UserSession session) {
        UserSession nonNullSession = Objects.requireNonNull(session, "session");
        User sanitizedUser = UserFactory.withoutIdentity(nonNullSession.getUser());
        UserSession sanitizedSession = UserSession.of(sanitizedUser, nonNullSession.getProvider());
        current.set(sanitizedSession);
        sessionStream.postValue(sanitizedSession);
        userStream.postValue(sanitizedUser);
    }

    public void updateUser(@NonNull User user) {
        UserSession session = current.get();
        UserSession sanitizedSession = UserSession.of(user, session.getProvider());
        current.set(sanitizedSession);
        current.set(sanitizedSession);
        sessionStream.postValue(sanitizedSession);
        userStream.postValue(user);
    }

    @NonNull
    public LiveData<UserSession> getSessionStream() {
        UserSession existing = current.get();
        if (existing != null && sessionStream.getValue() == null) {
            sessionStream.setValue(existing);
        }
        return sessionStream;
    }

    @NonNull
    public LiveData<User> getUserStream() {
        User existingUser = getCurrentUser();
        if (existingUser != null && userStream.getValue() == null) {
            userStream.setValue(existingUser);
        }
        return userStream;
    }

    public void clear() {
        current.set(null);
        sessionStream.postValue(null);
        userStream.postValue(null);
    }
}
