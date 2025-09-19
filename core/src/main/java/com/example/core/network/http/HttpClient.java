package com.example.core.network.http;

import java.io.IOException;

/**
 * Defines the contract for executing HTTP requests without exposing the
 * underlying networking stack to higher layers.
 */
public interface HttpClient {

    HttpResponse get(String url) throws IOException;

    HttpResponse postJson(String url, String jsonBody) throws IOException;
}
