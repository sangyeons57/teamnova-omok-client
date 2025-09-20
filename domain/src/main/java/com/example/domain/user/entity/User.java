package com.example.domain.user.entity;

import java.util.Objects;

/**
 * Represents a user entity.
 */
public class User {
    private final String userId;
    private final String displayName;
    private final String profileIconCode;
    private final String role;
    private final String status;
    private final int score;

    public User(String userId, String displayName, String profileIconCode, String role, String status, int score) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileIconCode = profileIconCode;
        this.role = role;
        this.status = status;
        this.score = score;
    }

    // Getters for all fields
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getProfileIconCode() { return profileIconCode; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public int getScore() { return score; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return score == user.score &&
                Objects.equals(userId, user.userId) &&
                Objects.equals(displayName, user.displayName) &&
                Objects.equals(profileIconCode, user.profileIconCode) &&
                Objects.equals(role, user.role) &&
                Objects.equals(status, user.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, displayName, profileIconCode, role, status, score);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileIconCode='" + profileIconCode + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", score=" + score +
                '}';
    }
}
