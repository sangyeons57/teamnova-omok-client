package com.example.core_di;

import android.app.Application;

import com.example.core_api.token.TokenStore;
import com.example.infra.http.prefs.PrefsTokenStore;

/**
 * Supplies the shared {@link TokenStore} instance to presentation-layer clients.
 */
public class TokenContainer {
    private static volatile TokenStore instance;

    public static void init(Application app) {
        if (instance != null) return;
        synchronized (TokenContainer.class) {
            if (instance == null) {
                instance = new PrefsTokenStore(app);
            }
        }
    }

    public static TokenStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenContainer is not initialized");
        }
        return instance;
    }

    public static void setForTest(TokenStore fake) { instance = fake; }
}
