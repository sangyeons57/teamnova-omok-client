package com.example.data.datasource;

import com.example.core.network.http.HttpClient;
import com.example.core.network.http.HttpEndpointResolver;
import com.example.core.network.http.HttpResponse;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.ResponseList;
import com.example.data.model.http.response.ResponseSingle;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.Objects;

/**
 * Provides a single entry point for interacting with the PHP backend hosted under /public.
 */
public class DefaultPhpServerDataSource {

    private final HttpClient httpClient;
    private final Gson gson;

    public DefaultPhpServerDataSource(HttpClient httpClient) {
        this(httpClient, new Gson());
    }

    public DefaultPhpServerDataSource(HttpClient httpClient, Gson gson) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        this.gson = Objects.requireNonNull(gson, "gson");
    }

    public HttpResponse post(Request request) throws IOException {
        Objects.requireNonNull(request, "request");
        String jsonBody = request.toJson(gson);
        return executePost(request, jsonBody);
    }

    public HttpResponse postJson(String resourcePath, String jsonBody) throws IOException {
        Objects.requireNonNull(resourcePath, "resourcePath");
        Objects.requireNonNull(jsonBody, "jsonBody");
        String url = HttpEndpointResolver.resolvePublicPath(resourcePath);
        return httpClient.postJson(url, jsonBody);
    }

    public ResponseSingle postSingle(Request request) throws IOException {
        HttpResponse response = post(request);
        return parseResponse(response, ResponseSingle.class);
    }

    public ResponseList postList(Request request) throws IOException {
        HttpResponse response = post(request);
        return parseResponse(response, ResponseList.class);
    }

    private HttpResponse executePost(Request request, String jsonBody) throws IOException {
        String resolvedPath = request.getPath() != null ? request.getPath().toString() : null;
        String url = HttpEndpointResolver.resolvePublicPath(resolvedPath);
        return httpClient.postJson(url, jsonBody);
    }

    private <T> T parseResponse(HttpResponse response, Class<T> responseType) throws IOException {
        if (response == null) {
            throw new IOException("response == null");
        }
        String body = response.getBody();
        if (body == null || body.trim().isEmpty()) {
            throw new IOException("Expected JSON body but was empty");
        }
        try {
            T parsed = gson.fromJson(body, responseType);
            if (parsed == null) {
                throw new IOException("Failed to parse JSON into " + responseType.getSimpleName());
            }
            return parsed;
        } catch (JsonParseException exception) {
            throw new IOException("Malformed JSON received", exception);
        }
    }
}
