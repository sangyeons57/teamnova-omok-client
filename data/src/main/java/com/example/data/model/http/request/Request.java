package com.example.data.model.http.request;

import com.google.gson.Gson;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Request {
    private String apiVersion;
    private Path path;
    private UUID requestId = UUID.randomUUID();
    private Instant timestamp = Instant.now();
    private Map<String, String> body = new HashMap<>();

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(body);
    }

    public void setBody(Map<String, String> body) {
        this.body = body != null ? new HashMap<>(body) : new HashMap<>();
    }

    public Map<String, Object> toJsonStructure() {
        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotBlank(payload, "apiVersion", apiVersion);
        putIfNotBlank(payload, "requestId", requestId.toString());
        if (timestamp != null) {
            payload.put("timestamp", timestamp.toString());
        }
        if (path != null) {
            payload.put("path", path.toString());
        }
        if (!body.isEmpty()) {
            payload.put("body", new LinkedHashMap<>(body));
        }
        return payload;
    }

    public String toJson(Gson gson) {
        if (gson == null) {
            throw new IllegalArgumentException("gson == null");
        }
        return gson.toJson(toJsonStructure());
    }

    private static void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (key == null || value == null) {
            return;
        }
        String trimmed = value.trim();
        if (!trimmed.isEmpty()) {
            map.put(key, trimmed);
        }
    }
}
