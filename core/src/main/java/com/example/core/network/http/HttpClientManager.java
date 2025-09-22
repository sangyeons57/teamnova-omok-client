package com.example.core.network.http;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;
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

    private static final String TAG = "HttpClientManager";
    private static final String INDENT = "    ";

    private static final OkHttpClient REFRESH_CLIENT = new OkHttpClient.Builder().build();
    private static final HttpClientManager INSTANCE = new HttpClientManager(
            new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .authenticator(new TokenAuthenticator(REFRESH_CLIENT))
                    .build()
    );
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    private HttpClientManager(OkHttpClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    public static HttpClientManager getInstance() {
        return INSTANCE;
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

        logRequest(request, jsonBody);

        return execute(request);
    }

    private HttpResponse execute(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody != null ? responseBody.string() : "";

            logResponse(response, body);

            Map<String, String> headers = new HashMap<>();
            for (String name : response.headers().names()) {
                List<String> values = response.headers(name);
                headers.put(name, String.join(", ", values));
            }
            return new HttpResponse(response.code(), response.message(), body, headers);
        }
    }

    private static void logRequest(Request request, String body) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP Request\n")
                .append(INDENT).append("URL: ").append(request.url()).append('\n')
                .append(INDENT).append("Method: ").append(request.method()).append('\n')
                .append("Headers:\n").append(formatHeaders(request.headers())).append('\n')
                .append("Body:\n").append(formatBody(body));
        Log.d(TAG, builder.toString());
    }

    private static void logResponse(Response response, String body) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP Response\n")
                .append(INDENT).append("Code: ").append(response.code()).append('\n')
                .append(INDENT).append("Message: ").append(response.message()).append('\n')
                .append("Headers:\n").append(formatHeaders(response.headers())).append('\n')
                .append("Body:\n").append(formatBody(body));
        Log.d(TAG, builder.toString());
    }

    private static String formatHeaders(Headers headers) {
        if (headers == null || headers.size() == 0) {
            return INDENT + "(none)";
        }

        StringBuilder builder = new StringBuilder();
        for (String name : headers.names()) {
            builder.append(INDENT)
                    .append(name)
                    .append(": ")
                    .append(String.join(", ", headers.values(name)))
                    .append('\n');
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    private static String formatBody(String body) {
        if (body == null) {
            return INDENT + "(null)";
        }
        if (body.isEmpty()) {
            return INDENT + "(empty)";
        }

        String[] lines = body.split("\\r?\\n", -1);
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(INDENT).append(line).append('\n');
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }
}
