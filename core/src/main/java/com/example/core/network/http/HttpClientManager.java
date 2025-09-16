package com.example.core.network.http;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Provides a lightweight wrapper around OkHttp to keep HTTP logic within the core layer.
 */
public final class HttpClientManager {

    private static final String DEFAULT_BASE_URL = "https://bamsol.net";
    private static final HttpClientManager INSTANCE = new HttpClientManager(new OkHttpClient(), DEFAULT_BASE_URL);

    private final OkHttpClient client;
    private final String baseUrl;

    private HttpClientManager(OkHttpClient client, String baseUrl) {
        this.client = Objects.requireNonNull(client, "client");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
    }

    public static HttpClientManager getInstance() {
        return INSTANCE;
    }

    public static HttpClientManager from(OkHttpClient client) {
        return new HttpClientManager(client, DEFAULT_BASE_URL);
    }

    public static HttpClientManager from(OkHttpClient client, String baseUrl) {
        return new HttpClientManager(client, baseUrl);
    }

    public static HttpClientManager withBaseUrl(String baseUrl) {
        return new HttpClientManager(new OkHttpClient(), baseUrl);
    }

    public HttpResponse get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String payload = body.string();
            return new HttpResponse(
                    response.code(),
                    response.message(),
                    payload,
                    response.isSuccessful()
            );
        }
    }

    public HttpResponse getFromPath(String path) throws IOException {
        return get(resolveUrl(path));
    }

    private String resolveUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return baseUrl;
        }

        String trimmed = path.trim();
        if (isAbsolute(trimmed)) {
            return trimmed;
        }

        boolean pathStartsWithSlash = trimmed.startsWith("/");
        boolean baseEndsWithSlash = baseUrl.endsWith("/");

        if (pathStartsWithSlash && baseEndsWithSlash) {
            return baseUrl + trimmed.substring(1);
        }

        if (!pathStartsWithSlash && !baseEndsWithSlash) {
            return baseUrl + '/' + trimmed;
        }

        return baseUrl + trimmed;
    }

    private boolean isAbsolute(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }
}
