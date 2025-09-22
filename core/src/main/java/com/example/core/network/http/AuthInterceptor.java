package com.example.core.network.http;

import androidx.annotation.NonNull;

import com.example.core.token.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String at = TokenManager.getInstance().getAccessToken();
        Request request = chain.request();
        if (at != null && !at.isEmpty()) {
            request = request.newBuilder().header("Authorization", "Bearer " + at).build();
        }
        return chain.proceed(request);
    }
}
