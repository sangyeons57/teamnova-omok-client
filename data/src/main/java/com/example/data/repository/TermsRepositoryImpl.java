package com.example.data.repository;

import android.util.Log;

import com.example.application.port.out.TermsRepository;
import com.example.core.network.http.HttpClientManager;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.TermsAcceptanceRemoteException;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Error;
import com.example.data.model.http.response.ResponseSingle;
import com.example.domain.terms.value.Terms;

import java.io.IOException;
import java.time.Instant;
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

    public TermsRepositoryImpl() {
        this(new DefaultPhpServerDataSource(HttpClientManager.getInstance()));
    }

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
            ResponseSingle response = phpServerDataSource.postSingle(request);
            if (response.isError()) {
                throw new TermsAcceptanceRemoteException(
                        extractErrorMessage(response.getError(), "Failed to accept terms"));
            }
        } catch (IOException exception) {
            throw new TermsAcceptanceRemoteException("Failed to accept terms", exception);
        }
    }

    private Request buildRequest(Path path) {
        Request request = new Request();
        request.setPath(path);
        request.setRequestId(UUID.randomUUID());
        request.setTimestamp(Instant.now());
        return request;
    }

    private List<String> toJsonArray(List<Terms> acceptTypes) {
        List<String> array = new ArrayList<>(acceptTypes.size());
        for (Terms terms : acceptTypes) {
            array.add(terms.name());
        }
        return array;
    }

    private String extractErrorMessage(Error error, String defaultMessage) {
        if (error == null) {
            return defaultMessage;
        }
        String message = error.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            return message;
        }
        return defaultMessage;
    }
}
