package com.example.core.network.http;

import java.io.IOException;

/**
 * Defines the contract for executing HTTP requests without exposing the
 * underlying networking stack to higher layers.
 */
public interface HttpClient {

    HttpResponse post(HttpRequest request) throws IOException;
}
