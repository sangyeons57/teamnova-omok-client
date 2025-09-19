package com.example.core.network.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Provides a lightweight wrapper around OkHttp while exposing only the
 * {@link HttpClient} abstraction to other modules.
 */
public final class HttpClientManager implements HttpClient {

    private static final HttpClientManager INSTANCE = new HttpClientManager(new OkHttpClient());
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    private HttpClientManager(OkHttpClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    public static HttpClientManager getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse get(String url) throws IOException {
        Objects.requireNonNull(url, "url");

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return execute(request);
    }

    @Override
    public HttpResponse postJson(String url, String jsonBody) throws IOException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(jsonBody, "jsonBody");

        RequestBody requestBody = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        return execute(request);
    }

    private HttpResponse execute(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody != null ? responseBody.string() : "";
            Map<String, String> headers = new HashMap<>();
            for (String name : response.headers().names()) {
                List<String> values = response.headers(name);
                headers.put(name, String.join(", ", values));
            }
            return new HttpResponse(response.code(), response.message(), body, headers);
        }
    }
}
