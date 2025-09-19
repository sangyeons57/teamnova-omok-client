package com.example.core.network.http;

/**
 * Provides shared helpers for generating fully qualified HTTP endpoints used by the app.
 */
public final class HttpEndpointResolver {

    private static final String DEFAULT_PUBLIC_BASE_PATH = "https://bamsol.net/public/";

    private HttpEndpointResolver() {
        // Utility class, no instances.
    }

    public static String resolvePublicPath(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return DEFAULT_PUBLIC_BASE_PATH;
        }
        String trimmed = relativePath.trim();
        return DEFAULT_PUBLIC_BASE_PATH + '/' + trimmed;
    }
}
