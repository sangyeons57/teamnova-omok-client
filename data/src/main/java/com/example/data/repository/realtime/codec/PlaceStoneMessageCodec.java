package com.example.data.repository.realtime.codec;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * Encodes PLACE_STONE payloads sent to the realtime server.
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
}
