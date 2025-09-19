package com.example.data.common.model;

import java.util.Objects;

/**
 * Raw DTO representing the guest sign-up response from the backend.
 */
public class GuestSignupResponse {

    private final String message;

    public GuestSignupResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestSignupResponse that = (GuestSignupResponse) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "GuestSignupResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
