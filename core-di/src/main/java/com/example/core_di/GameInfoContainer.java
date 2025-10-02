package com.example.core_di;

import com.example.application.session.GameInfoStore;

/**
 * Provides access to the singleton GameInfoStore instance.
 */
public final class GameInfoContainer {
    private static volatile GameInfoContainer instance;

    public static void init() {
        if (instance != null) {
            return;
        }
        synchronized (GameInfoContainer.class) {
            if (instance == null) {
                instance = new GameInfoContainer();
            }
        }
    }

    public static GameInfoContainer getInstance() {
        GameInfoContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("GameInfoContainer is not initialized");
        }
        return container;
    }

    private final GameInfoStore gameInfoStore;

    private GameInfoContainer() {
        this.gameInfoStore = new GameInfoStore();
    }

    public GameInfoStore getStore() {
        return gameInfoStore;
    }
}
