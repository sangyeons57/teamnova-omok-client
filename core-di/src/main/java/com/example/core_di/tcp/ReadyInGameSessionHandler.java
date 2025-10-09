package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles READY_IN_GAME_SESSION frames emitted by the server to reflect player readiness.
 */
public final class ReadyInGameSessionHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "ReadyInGameSessionHdl";

    private final GameInfoStore gameInfoStore;

    public ReadyInGameSessionHandler(@NonNull GameInfoStore gameInfoStore) {
        super(TAG, "READY_IN_GAME_SESSION");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
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
    }
}
