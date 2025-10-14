package com.example.core_di;

import com.example.application.session.postgame.PostGameSessionStore;

/**
 * Provides access to the singleton PostGameSessionStore instance.
 */
public final class PostGameSessionContainer {

    private static volatile PostGameSessionContainer instance;

    public static void init() {
        if (instance != null) {
            return;
        }
        synchronized (PostGameSessionContainer.class) {
            if (instance == null) {
                instance = new PostGameSessionContainer();
            }
        }
    }

    public static PostGameSessionContainer getInstance() {
        PostGameSessionContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("PostGameSessionContainer is not initialized");
        }
        return container;
    }

    private final PostGameSessionStore store;

    private PostGameSessionContainer() {
        this.store = new PostGameSessionStore();
    }

    public PostGameSessionStore getStore() {
        return store;
    }
}
