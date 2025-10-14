package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.postgame.PlayerDisconnectReason;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles GAME_SESSION_PLAYER_DISCONNECTED frames to disable inactive players in the UI.
 */
public final class GameSessionPlayerDisconnectedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "GamePlayerDisconnectedHdl";

    private final PostGameSessionStore postGameSessionStore;

    public GameSessionPlayerDisconnectedHandler(@NonNull PostGameSessionStore postGameSessionStore) {
        super(TAG, "GAME_SESSION_PLAYER_DISCONNECTED");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String userId = root.optString("userId", "");
        String reasonLabel = root.optString("reason", "");

        if (sessionId.isEmpty() || userId.isEmpty()) {
            Log.w(TAG, "Player disconnected payload missing identifiers → sessionId=" + sessionId
                    + ", userId=" + userId);
            return;
        }

        PlayerDisconnectReason reason = PlayerDisconnectReason.fromLabel(reasonLabel);
        Log.d(TAG, "Player disconnected → sessionId=" + sessionId
                + ", userId=" + userId
                + ", reason=" + reason);

        postGameSessionStore.markPlayerDisconnected(sessionId, userId, reason);
    }
}
