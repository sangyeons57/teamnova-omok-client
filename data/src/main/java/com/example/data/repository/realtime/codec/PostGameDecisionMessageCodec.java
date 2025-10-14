package com.example.data.repository.realtime.codec;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Encodes requests and decodes responses for POST_GAME_DECISION frames.
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

    @NonNull
    public static PostGameDecisionAck decodeAck(byte[] payload) {
        String payloadText = payload != null
                ? new String(payload, StandardCharsets.UTF_8).trim()
                : "";
        if (payloadText.isEmpty()) {
            return PostGameDecisionAck.unknown("");
        }
        try {
            JSONObject root = new JSONObject(payloadText);
            String statusLabel = root.optString("status", "");
            PostGameDecisionAck.Status status = PostGameDecisionAck.parseStatus(statusLabel);

            if (status == PostGameDecisionAck.Status.ERROR) {
                String reasonLabel = root.optString("reason", "");
                PostGameDecisionAck.ErrorReason reason = PostGameDecisionAck.parseReason(reasonLabel);
                return PostGameDecisionAck.error(reason, payloadText);
            }

            String decisionLabel = root.optString("decision", "");
            PostGameDecisionOption decision = PostGameDecisionOption.fromLabel(decisionLabel);
            return PostGameDecisionAck.ok(decision, payloadText);
        } catch (JSONException e) {
            return PostGameDecisionAck.unknown(payloadText);
        }
    }
}
