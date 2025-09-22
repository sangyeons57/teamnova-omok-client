package com.example.core.token;

import androidx.annotation.NonNull;

/**
 * Supplies the shared {@link TokenManager} instance to presentation-layer clients.
 */
public interface TokenManagerProvider {
    @NonNull
    TokenManager getTokenManager();
}
