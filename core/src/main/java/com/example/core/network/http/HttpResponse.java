package com.example.core.network.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the outcome of an HTTP call with the most relevant response data.
 */
public final class HttpResponse {

    private final int code;
    private final String message;
    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(int code, String message, String body, Map<String, String> headers) {
        this.code = code;
        this.message = message != null ? message : "";
        this.body = body != null ? body : "";
        Map<String, String> safeHeaders = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.headers = Collections.unmodifiableMap(safeHeaders);
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", bodyLength=" + body.length() +
                ", headers=" + headers +
                '}';
    }
}
