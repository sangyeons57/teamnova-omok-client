package com.example.application.session;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Immutable value object describing a participant in a game session.
 */
public final class GameParticipantInfo {

    private final String userId;
    private final String displayName;
    private final int profileIconCode;

    public GameParticipantInfo(@NonNull String userId,
                               @NonNull String displayName,
                               int profileIconCode) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.profileIconCode = profileIconCode;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public int getProfileIconCode() {
        return profileIconCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameParticipantInfo that = (GameParticipantInfo) o;
        return profileIconCode == that.profileIconCode
                && userId.equals(that.userId)
                && displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, displayName, profileIconCode);
    }

    @Override
    public String toString() {
        return "GameParticipantInfo{userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileIconCode=" + profileIconCode +
                '}';
    }
}

