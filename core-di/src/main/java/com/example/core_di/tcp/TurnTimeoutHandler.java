package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.protocol.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Handles TURN_TIMEOUT broadcasts announcing that a player's timer expired.
 */
public final class TurnTimeoutHandler implements ClientFrameHandler {

    private static final String TAG = "TurnTimeoutHandler";

    private final GameInfoStore gameInfoStore;

    public TurnTimeoutHandler(@NonNull GameInfoStore gameInfoStore) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            Log.w(TAG, "Received null frame for TURN_TIMEOUT");
            return ClientDispatchResult.continueDispatch();
        }
        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            Log.w(TAG, "TURN_TIMEOUT payload missing");
            return ClientDispatchResult.continueDispatch();
        }

        String raw = new String(payload, StandardCharsets.UTF_8);
        Log.d(TAG, "TURN_TIMEOUT payload: " + raw);

        try {
            JSONObject root = new JSONObject(raw);
            String sessionId = root.optString("sessionId", "");
            String timedOutUserId = root.optString("timedOutUserId", null);

            Log.i(TAG, "Turn timeout → sessionId=" + sessionId + ", timedOutUserId=" + timedOutUserId);

            JSONObject turnJson = root.optJSONObject("turn");
            TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse TURN_TIMEOUT payload", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error handling TURN_TIMEOUT frame", e);
        }

        return ClientDispatchResult.continueDispatch();
    }
}
