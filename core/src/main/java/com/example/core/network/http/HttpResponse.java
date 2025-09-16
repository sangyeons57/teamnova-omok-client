package com.example.core.network.http;

import java.util.Objects;

/**
 * Immutable value object representing an HTTP response returned by the core HTTP client.
 */
public class HttpResponse {

    private final int code;
    private final String message;
    private final String body;
    private final boolean successful;

    public HttpResponse(int code, String message, String body, boolean successful) {
        this.code = code;
        this.message = message;
        this.body = body;
        this.successful = successful;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getBody() {
        return body;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponse that = (HttpResponse) o;
        return code == that.code &&
                successful == that.successful &&
                Objects.equals(message, that.message) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, body, successful);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body='" + body + '\'' +
                ", successful=" + successful +
                '}';
    }
}
