package com.example.data.common.datasource;

import android.net.Uri;

import com.example.core.network.http.HttpClientManager;
import com.example.core.network.http.HttpResponse;
import com.example.data.common.model.request.Request;
import com.example.data.common.model.response.ResponseList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Provides a single entry point for interacting with the PHP backend hosted under /public.
 */
public class DefaultPhpServerDataSource {

    private static final String DEFAULT_PUBLIC_BASE_PATH = "https://bamsol.net/public/";
    private final HttpClientManager httpClientManager;
    private final Gson gson;


    public DefaultPhpServerDataSource(HttpClientManager httpClientManager) {
        this.httpClientManager = Objects.requireNonNull(httpClientManager, "httpClientManager");
        this.gson = new Gson();
    }


    /**
     * Executes a GET request after building the URL with parameters from the Request object.
     * @param resourcePath The base path for the API endpoint.
     * @param request The request object containing query parameters.
     * @return The HttpResponse from the server.
     * @throws IOException If a network error occurs.
     */
    public HttpResponse get(Request request) throws IOException {
        String baseUrl = resolvePublicPath(request.getPath().toString());

        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        for (HashMap.Entry<String, String> entry : request.getBody().entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        uriBuilder.appendQueryParameter("apiVersion", request.getApiVersion());
        uriBuilder.appendQueryParameter("requestId", request.getRequestId());
        uriBuilder.appendQueryParameter("timestamp", request.getTimestamp().toString());

        Response response = httpClientManager.get(uriBuilder.build().toString());
        return
    }


    public HttpResponse postJson(String resourcePath, String jsonBody) throws IOException {
        String url = resolvePublicPath(resourcePath);
        return httpClientManager.postJson(url, jsonBody);
    }

    /**
     * Executes a POST request after serializing the Request object's body to JSON.
     * @param resourcePath The base path for the API endpoint.
     * @param request The request object containing the body to be sent as JSON.
     * @return The HttpResponse from the server.
     * @throws IOException If a network error occurs.
     */
    public HttpResponse post(String resourcePath, Request request) throws IOException {
        String jsonBody = gson.toJson(request.getBody());
        return postJson(resourcePath, jsonBody);
    }

    private String buildUrlForGet(String resourcePath, HashMap<String, String> params) {

    }


    private static String resolvePublicPath(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return DEFAULT_PUBLIC_BASE_PATH;
        }

        String trimmed = relativePath.trim();
        if (isAbsolute(trimmed)) {
            return trimmed;
        }

        return DEFAULT_PUBLIC_BASE_PATH + trimmed;
    }

    private static boolean isAbsolute(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

}
