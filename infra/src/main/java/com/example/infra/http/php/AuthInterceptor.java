package com.example.infra.http.php;

import androidx.annotation.NonNull;

import com.example.core_api.token.TokenStore;

import java.io.IOException;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenStore tokenStore;
    private static final Set<String> BYPASS_PATHS = Set.of(
            "/public/refresh-token.php",  "/public/create-account.php"
    );
    public AuthInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        if(BYPASS_PATHS.contains(path)) {
            return chain.proceed(request);
        }
        String at = tokenStore.getAccessToken();
        if (at != null && !at.isEmpty()) {
            request = request.newBuilder().header("Authorization", "Bearer " + at).build();
        }

        return chain.proceed(request);
    }
}
