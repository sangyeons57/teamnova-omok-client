package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles TURN_TIMEOUT broadcasts announcing that a player's timer expired.
 */
public final class TurnTimeoutHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "TurnTimeoutHandler";

    private final GameInfoStore gameInfoStore;

    public TurnTimeoutHandler(@NonNull GameInfoStore gameInfoStore) {
        super(TAG, "TURN_TIMEOUT");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String timedOutUserId = root.optString("timedOutUserId", null);

        Log.i(TAG, "Turn timeout → sessionId=" + sessionId + ", timedOutUserId=" + timedOutUserId);

        JSONObject turnJson = root.optJSONObject("turn");
        TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
    }
}
