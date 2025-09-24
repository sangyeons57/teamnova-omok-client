package com.example.application.port.out.user;

import com.example.domain.user.value.Terms;

import java.util.List;

/**
 * Provides access to terms related remote operations.
 */
public interface TermsRepository {

    void acceptTerms(String accessToken, List<Terms> acceptTypes);
}
