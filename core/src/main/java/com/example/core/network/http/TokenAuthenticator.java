package com.example.core.network.http;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core.token.TokenManager;

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
    private final Object lock;
    private final TokenManager tokenManager;

    private final static String URL = "https://bamsol.net/public/refresh-token.php";

    public TokenAuthenticator(OkHttpClient refreshClient) {
        this.refreshClient = Objects.requireNonNull(refreshClient, "refreshClient");
        this.tokenManager = TokenManager.getInstance();
        this.lock = new Object();
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) > 1) return null;
        if(response.code() == 401) return null;

        synchronized (lock) {
            String reqAuth = response.request().header("Authorization");
            if(("Bearer " + tokenManager.getAccessToken()).equals(reqAuth)) {
                if (!refreshBlocking()) return null;
            }

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                    .build();
        }
    }

    private boolean refreshBlocking() throws IOException {
        Request request = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(
                        "{\"refresh_token\":\""+tokenManager.getRefreshToken()+"\"}",
                        MediaType.parse("application/json; charset=urf-8")
                ))
                .build();
        try (Response r = refreshClient.newCall(request).execute()) {
            if(!r.isSuccessful()) return false;
            String body = Objects.requireNonNull(r.body()).string();
            JSONObject json = new JSONObject(body);
            tokenManager.saveTokens( json.getString("access_token"), json.getString("refresh_token"));
            //파싱  + 저장
        } catch (JSONException e) {
            Log.e("TokenAuthenticator", "JSON parsing error:" + e);
        }
        return true;
    }

    private int responseCount(Response r) {
        int c = 1; while ((r = r.priorResponse()) != null) c++; return c;
    }
}
