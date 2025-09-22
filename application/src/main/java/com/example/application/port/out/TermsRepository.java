package com.example.application.port.out;

import com.example.domain.terms.value.Terms;

import java.util.List;

/**
 * Provides access to terms related remote operations.
 */
public interface TermsRepository {

    void acceptTerms(String accessToken, List<Terms> acceptTypes);
}
