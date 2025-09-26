package com.example.domain.user.entity;

import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserProfileIcon;
import com.example.domain.user.value.UserRole;
import com.example.domain.user.value.UserScore;
import com.example.domain.user.value.UserStatus;

import java.util.Objects;

/**
 * ==== Aggregate Root =====
 * Represents a user entity.
 */
public final class User  {
    private UserId userId;
    private UserDisplayName displayName;
    private UserProfileIcon profileIcon;
    private UserRole role;
    private UserStatus status;
    private UserScore score;
    private Identity identity;

    private User(UserId userId, UserDisplayName displayName, UserProfileIcon profileIcon, UserRole role, UserStatus status, UserScore score, Identity identity) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.profileIcon = Objects.requireNonNull(profileIcon, "profileIcon");
        this.role = Objects.requireNonNull(role, "role");
        this.status = Objects.requireNonNull(status, "status");
        this.score = Objects.requireNonNull(score, "score");
        this.identity = Objects.requireNonNull(identity, "identity");
    }

    public static User of(UserId userId, UserDisplayName displayName, UserProfileIcon profileIcon, UserRole role, UserStatus status, UserScore score, Identity identity) {
        return new User(userId, displayName, profileIcon, role, status, score, identity);
    }

    public UserId getUserId() {
        return userId;
    }
    public UserDisplayName getDisplayName() {
        return displayName;
    }
    public UserProfileIcon getProfileIcon() {
        return profileIcon;
    }
    public UserRole getRole() {
        return role;
    }
    public UserStatus getStatus() {
        return status;
    }
    public UserScore getScore() {
        return score;
    }
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId: " + userId.getValue() +
                ", displayName: " + displayName.getValue() +
                ", profileIcon: " + profileIcon.getValue() +
                ", rol: " + role.getValue() +
                ", status: " + status.getValue() +
                ", score: " + score.getValue() +
                ", identity: " + identity +
                '}';
    }
}
