package com.example.feature_game.game.presentation.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * UI model representing a seat in the game info dialog.
 */
public final class GameInfoSlot {

    private final int index;
    private final String userId;
    private final String displayName;
    private final boolean occupied;
    private final boolean enabled;
    private final int profileIconCode;

    public GameInfoSlot(int index,
                        @NonNull String userId,
                        @NonNull String displayName,
                        boolean occupied,
                        boolean enabled,
                        int profileIconCode) {
        this.index = index;
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.occupied = occupied;
        this.enabled = enabled;
        this.profileIconCode = profileIconCode;
    }

    public int getIndex() {
        return index;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getProfileIconCode() {
        return profileIconCode;
    }
}
