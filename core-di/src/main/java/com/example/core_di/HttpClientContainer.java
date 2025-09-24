package com.example.core_di;

import com.example.core.network.http.HttpClient;
import com.example.infra.http.php.HttpClientManager;

public class HttpClientContainer {
    private static HttpClientContainer instance;
    public static HttpClientContainer getInstance() {
        if (instance == null) {
            instance = new HttpClientContainer();
        }
        return instance;
    }
    private final HttpClientManager httpClientManager;
    public  HttpClientContainer() {
        httpClientManager = new HttpClientManager(
                TokenContainer.getInstance(),
                EventBusContainer.getInstance());
    }

    public HttpClient get() {
        return httpClientManager;
    }
}
