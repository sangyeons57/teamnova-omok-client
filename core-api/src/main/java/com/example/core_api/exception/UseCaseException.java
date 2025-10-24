package com.example.core_api.exception;

import java.util.Map;
import java.util.Objects;

public class UseCaseException extends RuntimeException {

    private final String code;

    public UseCaseException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public UseCaseException(String code, String message, Map<String, Objects> context) {
        super(message);
        this.code = code;
    }
    
    public UseCaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() { return code; }

    public static UseCaseException of(String code, String message) {
        return new UseCaseException(code, message);
    }
}
