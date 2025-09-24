package com.example.data.repository.user;

import android.util.Log;

import com.example.application.port.out.user.TermsRepository;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.TermsAcceptanceRemoteException;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;
import com.example.domain.user.value.Terms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation for terms related repository operations.
 */
public class TermsRepositoryImpl implements TermsRepository {

    private static final String LOG_TAG = "TermsRepository";

    private final DefaultPhpServerDataSource phpServerDataSource;

    public TermsRepositoryImpl(DefaultPhpServerDataSource phpServerDataSource) {
        this.phpServerDataSource = Objects.requireNonNull(phpServerDataSource, "phpServerDataSource");
    }

    @Override
    public void acceptTerms(String accessToken, List<Terms> acceptTypes) {
        Objects.requireNonNull(accessToken, "accessToken");
        Objects.requireNonNull(acceptTypes, "acceptTypes");
        if (acceptTypes.isEmpty()) {
            throw new IllegalArgumentException("acceptTypes must not be empty");
        }

        try {
            Request request = buildRequest(Path.TERMS_ACCEPTANCES);
            Map<String, Object> body = new HashMap<>();
            body.put("access_token", accessToken.trim());
            body.put("accept_types", toJsonArray(acceptTypes));
            request.setBody(body);

            Log.d(LOG_TAG, "acceptTerms request=" + body);
            Response response = phpServerDataSource.post(request);
            if (!response.isSuccess()) {
                throw new TermsAcceptanceRemoteException("Failed to accept terms Status: " + response.statusCode() + " | " + response.statusMessage() + " |" + response.body());
            }
        } catch (IOException exception) {
            throw new TermsAcceptanceRemoteException("Failed to accept terms", exception);
        }
    }

    private Request buildRequest(Path path) {
        Request request = new Request();
        request.setPath(path);
        request.setRequestId(UUID.randomUUID());
        return request;
    }

    private List<String> toJsonArray(List<Terms> acceptTypes) {
        List<String> array = new ArrayList<>(acceptTypes.size());
        for (Terms terms : acceptTypes) {
            array.add(terms.name());
        }
        return array;
    }
}
