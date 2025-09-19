package com.example.data.common.model;

import java.util.Objects;

/**
 * Raw DTO representing the hello world remote payload before mapping to the domain layer.
 */
public class HelloWorldResponse {

    private final String message;

    public HelloWorldResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelloWorldResponse that = (HelloWorldResponse) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "HelloWorldResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
