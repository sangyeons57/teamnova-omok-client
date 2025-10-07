package com.example.feature_game.game.presentation.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * UI model representing a participant slot around the game board.
 */
public final class GamePlayerSlot {

    private final int position;
    private final String displayName;
    private final boolean empty;
    private final boolean enabled;

    public GamePlayerSlot(int position, @NonNull String displayName, boolean empty, boolean enabled) {
        this.position = position;
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.empty = empty;
        this.enabled = enabled;
    }

    public int getPosition() {
        return position;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
