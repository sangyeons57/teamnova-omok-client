package com.example.data.auth.datasource;

import com.example.core.network.http.HttpClientManager;
import com.example.core.network.http.HttpResponse;
import com.example.data.auth.exception.HelloWorldRemoteException;
import com.example.data.auth.model.HelloWorldResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * Encapsulates the remote hello world data retrieval logic.
 */
public class HelloWorldRemoteDataSource {

    private static final String HELLO_WORLD_PATH = "/hello-world.php";

    private final HttpClientManager httpClientManager;
    private final String resourcePath;

    public HelloWorldRemoteDataSource() {
        this(HttpClientManager.getInstance(), HELLO_WORLD_PATH);
    }

    public HelloWorldRemoteDataSource(HttpClientManager httpClientManager) {
        this(httpClientManager, HELLO_WORLD_PATH);
    }

    public HelloWorldRemoteDataSource(HttpClientManager httpClientManager, String resourcePath) {
        this.httpClientManager = Objects.requireNonNull(httpClientManager, "httpClientManager");
        this.resourcePath = Objects.requireNonNull(resourcePath, "resourcePath");
    }

    public HelloWorldResponse getHelloWorld() {
        try {
            HttpResponse response = httpClientManager.getFromPath(resourcePath);
            if (!response.isSuccessful()) {
                String message = "Unexpected HTTP " + response.getCode() + ' ' + response.getMessage();
                throw new HelloWorldRemoteException(message);
            }
            return new HelloWorldResponse(response.getBody());
        } catch (IOException exception) {
            throw new HelloWorldRemoteException("Failed to fetch hello world message", exception);
        }
    }
}
