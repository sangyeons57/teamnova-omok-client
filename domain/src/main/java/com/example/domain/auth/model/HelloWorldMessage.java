package com.example.domain.auth.model;

import java.util.Objects;

/**
 * Represents the domain model for the hello world message delivered from the backend.
 */
public class HelloWorldMessage {

    private final String message;

    public HelloWorldMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelloWorldMessage that = (HelloWorldMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "HelloWorldMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
