package com.example.data.repository.realtime.codec;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionOption;

import java.nio.charset.StandardCharsets;

/**
 * Encodes POST_GAME_DECISION request payloads.
 */
public final class PostGameDecisionMessageCodec {

    private PostGameDecisionMessageCodec() {
        // Utility class
    }

    @NonNull
    public static byte[] encode(@NonNull PostGameDecisionOption decision) {
        if (decision == null) {
            throw new IllegalArgumentException("decision == null");
        }
        if (decision == PostGameDecisionOption.UNKNOWN) {
            throw new IllegalArgumentException("decision must be REMATCH or LEAVE");
        }
        return decision.name().getBytes(StandardCharsets.UTF_8);
    }

}
