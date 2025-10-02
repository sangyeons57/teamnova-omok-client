package com.example.core_di;

import com.example.application.session.UserSessionStore;

/**
 * Provides access to the singleton UserSessionStore instance.
 */
public final class UserSessionContainer {
    private static volatile UserSessionContainer instance;

    public static void init() {
        if (instance != null) {
            return;
        }
        synchronized (UserSessionContainer.class) {
            if (instance == null) {
                instance = new UserSessionContainer();
            }
        }
    }

    public static UserSessionContainer getInstance() {
        UserSessionContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("UserSessionContainer is not initialized");
        }
        return container;
    }

    private final UserSessionStore userSessionStore;

    private UserSessionContainer() {
        this.userSessionStore = new UserSessionStore();
    }

    public UserSessionStore getStore() {
        return userSessionStore;
    }
}
