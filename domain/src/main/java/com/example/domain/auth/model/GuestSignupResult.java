package com.example.domain.auth.model;

import java.util.Objects;

/**
 * Represents the structured result of a guest sign-up operation.
 */
public class GuestSignupResult {

    private final boolean isSuccess;
    private final User user;
    private final String accessToken;
    private final String refreshToken;
    private final String errorMessage;

    // Private constructor to force using static factory methods
    private GuestSignupResult(boolean isSuccess, User user, String accessToken, String refreshToken, String errorMessage) {
        this.isSuccess = isSuccess;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a success result.
     */
    public static GuestSignupResult success(User user, String accessToken, String refreshToken) {
        return new GuestSignupResult(true, user, accessToken, refreshToken, null);
    }

    /**
     * Creates a failure result.
     */
    public static GuestSignupResult failure(String errorMessage) {
        return new GuestSignupResult(false, null, null, null, errorMessage);
    }

    // Getters
    public boolean isSuccess() { return isSuccess; }
    public User getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestSignupResult that = (GuestSignupResult) o;
        return isSuccess == that.isSuccess &&
                Objects.equals(user, that.user) &&
                Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess, user, accessToken, refreshToken, errorMessage);
    }

    @Override
    public String toString() {
        return "GuestSignupResult{" +
                "isSuccess=" + isSuccess +
                ", user=" + user +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
