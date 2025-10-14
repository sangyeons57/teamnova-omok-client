package com.example.feature_game.game.presentation.model;

import androidx.annotation.NonNull;

import com.example.application.session.postgame.PlayerDisconnectReason;

import java.util.Objects;

/**
 * UI model representing a participant slot around the game board.
 */
public final class GamePlayerSlot {

    private final int position;
    private final String userId;
    private final String displayName;
    private final boolean empty;
    private final boolean enabled;
    private final int profileIconCode;
    private final PlayerDisconnectReason disconnectReason;

    public GamePlayerSlot(int position,
                          @NonNull String userId,
                          @NonNull String displayName,
                          boolean empty,
                          boolean enabled,
                          int profileIconCode,
                          @NonNull PlayerDisconnectReason disconnectReason) {
        this.position = position;
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.empty = empty;
        this.enabled = enabled;
        this.profileIconCode = profileIconCode;
        this.disconnectReason = Objects.requireNonNull(disconnectReason, "disconnectReason");
    }

    public int getPosition() {
        return position;
    }

    @NonNull
    public String getUserId() {
        return userId;
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

    public int getProfileIconCode() {
        return profileIconCode;
    }

    @NonNull
    public PlayerDisconnectReason getDisconnectReason() {
        return disconnectReason;
    }

    public boolean isDisconnected() {
        return disconnectReason != PlayerDisconnectReason.UNKNOWN;
    }
}
