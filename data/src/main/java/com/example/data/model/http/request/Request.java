package com.example.data.model.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Request {
    private Path path;
    private UUID requestId = UUID.randomUUID();
    private Map<String, Object> body = new HashMap<>();

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

    public Map<String, Object> getBody() {
        return Collections.unmodifiableMap(body);
    }

    public void setBody(Map<String, ?> body) {
        if (body == null || body.isEmpty()) {
            this.body = new HashMap<>();
            return;
        }
        Map<String, Object> copy = new HashMap<>(body.size());
        copy.putAll(body);
        this.body = copy;
    }

    public static Request defaultRequest(Path path) {
        Request request = new Request();
        request.setPath(path);
        request.setRequestId(UUID.randomUUID());
        return request;
    }
}
