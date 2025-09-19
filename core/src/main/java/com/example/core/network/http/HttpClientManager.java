package com.example.core.network.http;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Provides a lightweight wrapper around OkHttp to keep HTTP logic within the core layer.
 */
public final class HttpClientManager {

    private static final HttpClientManager INSTANCE = new HttpClientManager(new OkHttpClient());

    private final OkHttpClient client;

    private HttpClientManager(OkHttpClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    public static HttpClientManager getInstance() {
        return INSTANCE;
    }

    public Response get(String url) throws IOException {
        Objects.requireNonNull(url, "url");

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return execute(request);
    }

    public Response postJson(String url, String jsonBody) throws IOException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(jsonBody, "jsonBody");

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        return execute(request);
    }

    private Response execute(Request request) throws IOException {
        return client.newCall(request).execute();
    }
}
