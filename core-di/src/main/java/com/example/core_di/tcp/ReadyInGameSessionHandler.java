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
 * Handles READY_IN_GAME_SESSION frames emitted by the server to reflect player readiness.
 */
public final class ReadyInGameSessionHandler implements ClientFrameHandler {

    private static final String TAG = "ReadyInGameSessionHdl";

    private final GameInfoStore gameInfoStore;

    public ReadyInGameSessionHandler(@NonNull GameInfoStore gameInfoStore) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            Log.w(TAG, "Received null frame for READY_IN_GAME_SESSION");
            return ClientDispatchResult.continueDispatch();
        }
        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            Log.w(TAG, "READY_IN_GAME_SESSION payload missing");
            return ClientDispatchResult.continueDispatch();
        }

        String raw = new String(payload, StandardCharsets.UTF_8);
        Log.d(TAG, "READY_IN_GAME_SESSION payload: " + raw);

        try {
            JSONObject root = new JSONObject(raw);
            String sessionId = root.optString("sessionId", "");
            String userId = root.optString("userId", "");
            boolean ready = root.optBoolean("ready", false);
            boolean allReady = root.optBoolean("allReady", false);
            boolean gameStarted = root.optBoolean("gameStarted", false);

            Log.i(TAG, "Ready state changed → sessionId=" + sessionId
                    + ", userId=" + userId
                    + ", ready=" + ready
                    + ", allReady=" + allReady
                    + ", gameStarted=" + gameStarted);

            JSONObject turnJson = root.optJSONObject("turn");
            TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse READY_IN_GAME_SESSION payload", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error handling READY_IN_GAME_SESSION frame", e);
        }

        return ClientDispatchResult.continueDispatch();
    }
}
