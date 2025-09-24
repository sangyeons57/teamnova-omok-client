package com.example.infra.http.php;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core.event.AppEventBus;
import com.example.core.event.SessionInvalidatedEvent;
import com.example.core.token.TokenStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private final OkHttpClient refreshClient;
    private final Object lock = new Object();
    private final TokenStore tokenStore;
    private final AppEventBus eventBus;

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final static String URL = "https://bamsol.net/public/refresh-token.php";

    public TokenAuthenticator(OkHttpClient refreshClient, TokenStore tokenStore, AppEventBus eventBus) {
        this.refreshClient = Objects.requireNonNull(refreshClient, "refreshClient");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) >= 2) {
            handleRefreshFailure();
            return null;
        }

        String fresh = tokenStore.getAccessToken();
        String reqAuth = response.request().header("Authorization");
        if (fresh != null && !("Bearer " + fresh).equals(reqAuth)) {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + fresh)
                    .build();
        }

        synchronized (lock) {
            fresh = tokenStore.getAccessToken();
            if (fresh != null && !("Bearer " + fresh).equals(reqAuth)) {
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + fresh)
                        .build();
            }

            if(!refreshBlocking()) {
                handleRefreshFailure();
                return null;
            }

            String newAt = tokenStore.getAccessToken();
            if(newAt == null || newAt.isEmpty()) {
                handleRefreshFailure();
                return null;
            }

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newAt)
                    .build();

        }
    }

    private boolean refreshBlocking() {
        try {
            String rt = tokenStore.getRefreshToken();
            if (rt == null || rt.isEmpty()) return false;

            Request request = new Request.Builder()
                    .url(URL)
                    .post(RequestBody.create("{\"refresh_token\":\"" + rt + "\"}", MEDIA_TYPE)) // ★ utf-8
                    .header("Accept", "application/json")
                    .header("X-Request-ID", "rt-" + System.nanoTime())
                    .build();

            try (Response r = refreshClient.newCall(request).execute()) {
                int code = r.code();

                if (!r.isSuccessful()) {
                    // 본문을 안전하게 스니펫만 로깅(최대 N바이트)
                    String err = null;
                    try { err = r.peekBody(1024).string(); } catch (Exception ignore) {}
                    Log.e("TokenAuthenticator", "refresh failed: " + code + " body=" + err);
                    return false;
                }

                String body = r.body().string();
                if (body.isEmpty()) {
                    Log.e("TokenAuthenticator", "refresh empty body");
                    return false;
                }

                JSONObject json = new JSONObject(body);
                String at = json.optString("access_token", null);
                String newRt = json.optString("refresh_token", null);
                if (at.isEmpty() || newRt.isEmpty()) {
                    Log.e("TokenAuthenticator", "refresh invalid json: " + body);
                    return false;
                }

                tokenStore.saveTokens(at, newRt);
                return true;
            }
        } catch (Exception e) {
            Log.e("TokenAuthenticator", "refresh exception: " + e);
            return false;
        }
    }

    private void handleRefreshFailure() {
        tokenStore.clearAllTokens();
        eventBus.post(new SessionInvalidatedEvent(SessionInvalidatedEvent.Reason.TOKEN_REFRESH_FAILURE));
    }

    private int responseCount(Response r) {
        int c = 1; while ((r = r.priorResponse()) != null) c++; return c;
    }
}
