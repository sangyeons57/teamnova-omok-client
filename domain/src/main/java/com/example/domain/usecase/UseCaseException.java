package com.example.domain.usecase;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class UseCaseException extends RuntimeException {
    private final String code;
    private final Map<String, Objects> context;
    
    public UseCaseException(String code, String message) {
        super(message);
        this.code = code;
        this.context = Collections.emptyMap();
    }
    
    public UseCaseException(String code, String message, Map<String, Objects> context) {
        super(message);
        this.code = code;
        this.context = (context == null) ? Collections.emptyMap() : Collections.unmodifiableMap(context);
    }
    
    public UseCaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.context = Collections.emptyMap();
    }
    
    public String code() { return code; }
    public Map<String, Objects> context() { return context; }

    public static UseCaseException of(String code, String message) {
        return new UseCaseException(code, message);
    }
}
