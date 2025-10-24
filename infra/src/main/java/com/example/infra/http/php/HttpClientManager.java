package com.example.infra.http.php;

import android.util.Log;

import com.example.core_api.event.AppEventBus;
import com.example.core_api.network.http.HttpClient;
import com.example.core_api.network.http.HttpRequest;
import com.example.core_api.network.http.HttpResponse;
import com.example.core_api.token.TokenStore;
import com.example.infra.http.provider.OkHttpClientProvider;

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

/**
 * Provides a lightweight wrapper around OkHttp while exposing only the
 * {@link HttpClient} abstraction to other modules.
 */
public final class HttpClientManager implements HttpClient {

    private static final String TAG = "HttpClientManager";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public HttpClientManager(TokenStore tokenStore, AppEventBus eventBus) {
        Objects.requireNonNull(tokenStore, "tokenStore");
        Objects.requireNonNull(eventBus, "eventBus");
        this.client = OkHttpClientProvider.init(tokenStore, eventBus).get();
    }

    @Override
    public HttpResponse post(HttpRequest httpRequest) throws IOException {
        Objects.requireNonNull(httpRequest, "HttpRequest");

        Log.d("HttpClientManager", "Start post request: " + httpRequest.url());

        RequestBody requestBody = RequestBody.create(httpRequest.body(),JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(httpRequest.url())
                .headers(toHeaders(httpRequest.headers()))
                .post(requestBody)
                .build();

        // .header("Content-Type", "application/json; charset=UTF-8")

        return execute(request);
    }

    private HttpResponse execute(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();

            Map<String, String> headers = new HashMap<>();
            for (String name : response.headers().names()) {
                List<String> values = response.headers(name);
                headers.put(name, String.join(", ", values));
            }
            return new HttpResponse(response.isSuccessful(), response.code(), response.message(), headers, body);
        }
    }

    private Headers toHeaders(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey() != null && header.getValue() != null){
                builder.add(header.getKey(), header.getValue());
            }
        }
        return builder.build();
    }

}
