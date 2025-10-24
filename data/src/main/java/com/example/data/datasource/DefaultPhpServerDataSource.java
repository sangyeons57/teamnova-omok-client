package com.example.data.datasource;

import android.util.Log;

import com.example.core_api.network.http.HttpClient;
import com.example.core_api.network.http.HttpRequest;
import com.example.core_api.network.http.HttpResponse;
import com.example.core_api.util.JsonMaps;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a single entry point for interacting with the PHP backend hosted under /public.
 */
public class DefaultPhpServerDataSource {

    private final HttpClient httpClient;


    public DefaultPhpServerDataSource(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    }

    public Response post(Request request) throws IOException {
        Objects.requireNonNull(request, "request");

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json; charset=utf-8");
        header.put("Accept", "application/json");
        header.put("X-Request-ID", request.getRequestId().toString());

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                request.getPath().toBasePath(),
                header,
                new JSONObject(request.getBody()).toString()
        );

        HttpResponse response = httpClient.post(httpRequest);

        return parseResponse(response);
    }


    private Response parseResponse(HttpResponse response) throws IOException {
        if (response == null) {
            throw new IOException("response == null");
        }

        Log.d("DefaultPhpServerDataSource", "Start parse HttpResponse to Response: " + response);

        Map<String, Object> body = null;
        try {
            body = JsonMaps.toMap( new JSONObject(response.body()) );
        } catch (JSONException e) {
            Log.e("DefaultPhpServerDataSource", "Failed to parse body BODY:" + response.body());
            body = new HashMap<>();
            body.put("body", response.body());
        }

        return new Response(
                response.isSuccessful(),
                response.statusCode(),
                response.statusMessage(),
                response.headers(),
                body
        );
    }
}
