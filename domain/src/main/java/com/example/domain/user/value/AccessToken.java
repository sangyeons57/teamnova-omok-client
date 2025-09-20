package com.example.domain.user.value;

import android.os.Debug;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an access token issued during authentication.
 */
public final class AccessToken {

    private final String value;
    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$");

    public AccessToken(String value) {
        this.value = Objects.requireNonNull(value, "value");
        validateFormat(this.value);
    }

    public String value() {
        return value;
    }

    /**
     * Splits the JWT into its three parts: header, payload, and signature.
     *
     * @return An array of strings containing the header, payload, and signature.
     */
    public String[] splitToken() {
        String[] parts = value.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT token format: Expected 3 parts, got " + parts.length);
        }
        return parts;
    }

    /**
     * Decodes the payload from the JWT and returns it as a Map.
     * The payload is the second part of the token and contains claims.
     *
     * @return A map representing the JSON payload.
     */
    public Map<String, Object> getPayload() {
        String[] parts = splitToken();
        byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
        String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(decodedPayload);
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                map.put(key, jsonObject.get(key));
            }
        } catch (JSONException e) {
            Log.d("getPayload", "(return empty map) Error parsing payload: " + e.getMessage());
        }
        return map;
    }

    /**
     * Validates if the given token string matches the JWT format.
     *
     * @param token The token string to validate.
     */
    private void validateFormat(String token) {
        if (!JWT_PATTERN.matcher(token).matches()) {
            throw new RuntimeException("Invalid JWT token format.");
        }
    }
}
