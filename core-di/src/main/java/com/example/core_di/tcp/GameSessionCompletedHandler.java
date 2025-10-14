package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.postgame.GameOutcome;
import com.example.application.session.postgame.GameOutcomeResult;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles GAME_SESSION_COMPLETED frames and stores per-player outcomes.
 */
public final class GameSessionCompletedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "GameSessionCompletedHdl";

    private final PostGameSessionStore postGameSessionStore;

    public GameSessionCompletedHandler(@NonNull PostGameSessionStore postGameSessionStore) {
        super(TAG, "GAME_SESSION_COMPLETED");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        long startedAt = root.optLong("startedAt", 0L);
        long endedAt = root.optLong("endedAt", 0L);
        long durationMillis = root.optLong("durationMillis", 0L);
        int turnCount = root.optInt("turnCount", 0);
        JSONArray outcomesArray = root.optJSONArray("outcomes");
        List<GameOutcome> outcomes = new ArrayList<>();
        if (outcomesArray != null) {
            for (int i = 0; i < outcomesArray.length(); i++) {
                JSONObject outcomeJson = outcomesArray.optJSONObject(i);
                if (outcomeJson == null) {
                    continue;
                }
                String userId = outcomeJson.optString("userId", "");
                String resultLabel = outcomeJson.optString("result", "");
                GameOutcomeResult result = GameOutcomeResult.fromLabel(resultLabel);
                if (userId.isEmpty()) {
                    continue;
                }
                outcomes.add(new GameOutcome(userId, result));
            }
        }
        Log.i(TAG, "Session completed → sessionId=" + sessionId
                + ", outcomes=" + outcomes.size()
                + ", durationMillis=" + durationMillis
                + ", turnCount=" + turnCount);
        postGameSessionStore.updateOutcomes(sessionId, outcomes, startedAt, endedAt, durationMillis, turnCount);
    }
}
