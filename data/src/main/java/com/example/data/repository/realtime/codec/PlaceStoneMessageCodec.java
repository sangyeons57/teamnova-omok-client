package com.example.data.repository.realtime.codec;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PlaceStoneResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Encodes and decodes PLACE_STONE payloads exchanged with the realtime server.
 */
public final class PlaceStoneMessageCodec {

    private PlaceStoneMessageCodec() {
        // Utility class
    }

    @NonNull
    public static byte[] encode(int x, int y) {
        String payload = x + "," + y;
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    @NonNull
    public static PlaceStoneResponse decode(byte[] payloadBytes) {
        String payloadText = payloadBytes != null
                ? new String(payloadBytes, StandardCharsets.UTF_8).trim()
                : "";
        ParsedPayload parsed = parsePayload(payloadText);
        if (parsed.status == PlaceStoneResponse.Status.SUCCESS) {
            return PlaceStoneResponse.success(payloadText);
        }
        if (parsed.status == PlaceStoneResponse.Status.UNKNOWN) {
            return new PlaceStoneResponse(PlaceStoneResponse.Status.UNKNOWN, payloadText);
        }
        return PlaceStoneResponse.failure(parsed.status, payloadText);
    }

    @NonNull
    private static ParsedPayload parsePayload(String payloadText) {
        if (payloadText == null || payloadText.isEmpty()) {
            return ParsedPayload.unknown();
        }
        String trimmed = payloadText.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                JSONObject root = new JSONObject(trimmed);
                String statusLabel = root.optString("status", "");
                PlaceStoneResponse.Status status = PlaceStoneResponse.parseStatus(statusLabel);
                return new ParsedPayload(status);
            } catch (JSONException ignored) {
                // Fall back to token parsing when payload isn't strict JSON.
            }
        }
        return parseFromTokens(trimmed);
    }

    @NonNull
    private static ParsedPayload parseFromTokens(String text) {
        PlaceStoneResponse.Status status = PlaceStoneResponse.Status.UNKNOWN;
        String[] tokens = text.split("[,\\n]");
        for (String token : tokens) {
            String segment = token.trim();
            if (segment.isEmpty()) {
                continue;
            }
            int colonIndex = segment.indexOf(':');
            if (colonIndex < 0) {
                PlaceStoneResponse.Status candidate = PlaceStoneResponse.parseStatus(stripQuotes(segment));
                if (candidate != PlaceStoneResponse.Status.UNKNOWN) {
                    status = candidate;
                }
                continue;
            }
            String key = stripQuotes(segment.substring(0, colonIndex)).toLowerCase(Locale.US);
            String value = stripQuotes(segment.substring(colonIndex + 1));
            if ("status".equals(key)) {
                status = PlaceStoneResponse.parseStatus(value);
            }
        }
        return new ParsedPayload(status);
    }

    @NonNull
    private static String stripQuotes(@NonNull String raw) {
        String trimmed = raw.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private record ParsedPayload(PlaceStoneResponse.Status status) {
        static ParsedPayload unknown() {
            return new ParsedPayload(PlaceStoneResponse.Status.UNKNOWN);
        }
    }
}
