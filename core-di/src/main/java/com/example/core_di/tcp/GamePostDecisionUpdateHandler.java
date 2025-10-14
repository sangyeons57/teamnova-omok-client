package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.session.postgame.PostGameDecisionStatus;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles GAME_POST_DECISION_UPDATE frames broadcasting decision progress.
 */
public final class GamePostDecisionUpdateHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "PostDecisionUpdateHdl";

    private final PostGameSessionStore postGameSessionStore;

    public GamePostDecisionUpdateHandler(@NonNull PostGameSessionStore postGameSessionStore) {
        super(TAG, "GAME_POST_DECISION_UPDATE");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");

        JSONArray decisionsArray = root.optJSONArray("decisions");
        Map<String, PostGameDecisionOption> decisions = new LinkedHashMap<>();
        if (decisionsArray != null) {
            for (int i = 0; i < decisionsArray.length(); i++) {
                JSONObject decisionJson = decisionsArray.optJSONObject(i);
                if (decisionJson == null) {
                    continue;
                }
                String userId = decisionJson.optString("userId", "");
                String decisionLabel = decisionJson.optString("decision", "");
                if (userId.isEmpty()) {
                    continue;
                }
                decisions.put(userId, PostGameDecisionOption.fromLabel(decisionLabel));
            }
        }

        JSONArray remainingArray = root.optJSONArray("remaining");
        List<String> remaining = new ArrayList<>();
        if (remainingArray != null) {
            for (int i = 0; i < remainingArray.length(); i++) {
                String userId = remainingArray.optString(i, "");
                if (!userId.isEmpty()) {
                    remaining.add(userId);
                }
            }
        }

        Log.d(TAG, "Decision update → sessionId=" + sessionId
                + ", decided=" + decisions.size()
                + ", remaining=" + remaining.size());

        PostGameDecisionStatus status = new PostGameDecisionStatus(sessionId, decisions, remaining);
        postGameSessionStore.updateDecisionStatus(status);
    }
}
