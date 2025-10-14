package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.MatchState;
import com.example.application.session.postgame.PostGameSessionState;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles GAME_SESSION_TERMINATED frames indicating the session ended without a rematch.
 */
public final class GameSessionTerminatedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "GameSessionTerminatedHdl";

    private final PostGameSessionStore postGameSessionStore;
    private final GameInfoStore gameInfoStore;

    public GameSessionTerminatedHandler(@NonNull PostGameSessionStore postGameSessionStore,
                                        @NonNull GameInfoStore gameInfoStore) {
        super(TAG, "GAME_SESSION_TERMINATED");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        long startedAt = root.optLong("startedAt", 0L);
        long endedAt = root.optLong("endedAt", 0L);
        long durationMillis = root.optLong("durationMillis", 0L);
        int turnCount = root.optInt("turnCount", 0);
        JSONArray disconnectedArray = root.optJSONArray("disconnected");
        List<String> disconnected = new ArrayList<>();
        if (disconnectedArray != null) {
            for (int i = 0; i < disconnectedArray.length(); i++) {
                String userId = disconnectedArray.optString(i, "");
                if (!userId.isEmpty()) {
                    disconnected.add(userId);
                }
            }
        }

        Log.i(TAG, "Session terminated → sessionId=" + sessionId
                + ", disconnected=" + disconnected.size()
                + ", durationMillis=" + durationMillis
                + ", turnCount=" + turnCount);

        PostGameSessionState current = postGameSessionStore.getCurrentState();
        if (current != null
                && sessionId.equals(current.getSessionId())
                && current.isRematchStarted()) {
            Log.i(TAG, "Rematch already in progress. Ignoring termination for session " + sessionId);
            return;
        }

        postGameSessionStore.markTerminated(sessionId, disconnected, startedAt, endedAt, durationMillis, turnCount);
        gameInfoStore.updateMatchState(MatchState.IDLE);
    }
}
