package com.example.application.dto.command;

import com.example.domain.user.value.Terms;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Command object encapsulating the information required to accept terms.
 */
public final class AcceptTermsCommand {

    private final String accessToken;
    private final List<Terms> acceptTypes;

    private AcceptTermsCommand(String accessToken, List<Terms> acceptTypes) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("accessToken must not be null or blank");
        }
        Objects.requireNonNull(acceptTypes, "acceptTypes");
        if (acceptTypes.isEmpty()) {
            throw new IllegalArgumentException("acceptTypes must not be empty");
        }
        this.accessToken = accessToken.trim();
        this.acceptTypes = List.copyOf(acceptTypes);
    }

    public static AcceptTermsCommand of(String accessToken, List<Terms> acceptTypes) {
        return new AcceptTermsCommand(accessToken, acceptTypes);
    }

    public static AcceptTermsCommand acceptAll(String accessToken) {
        return new AcceptTermsCommand(accessToken, Arrays.asList(Terms.values()));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public List<Terms> getAcceptTypes() {
        return acceptTypes;
    }
}
