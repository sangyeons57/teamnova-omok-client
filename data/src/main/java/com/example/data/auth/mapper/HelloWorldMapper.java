package com.example.data.auth.mapper;

import com.example.data.auth.model.HelloWorldResponse;
import com.example.domain.auth.model.HelloWorldMessage;

/**
 * Converts hello world data transfer objects into domain models.
 */
public class HelloWorldMapper {

    public HelloWorldMessage toDomain(HelloWorldResponse response) {
        String message = response != null ? response.getMessage() : "";
        return new HelloWorldMessage(message);
    }
}
