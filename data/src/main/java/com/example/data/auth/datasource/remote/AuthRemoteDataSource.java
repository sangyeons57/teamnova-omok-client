package com.example.data.auth.datasource.remote;

import com.example.core.network.http.HttpClientManager;

import java.util.Objects;

public class AuthRemoteDataSource {
    private final HttpClientManager httpClientManager;

    public AuthRemoteDataSource() {
        this(HttpClientManager.getInstance());
    }

    public AuthRemoteDataSource(HttpClientManager httpClientManager) {
        this.httpClientManager = Objects.requireNonNull(httpClientManager, "httpClientManager");
    }
}
