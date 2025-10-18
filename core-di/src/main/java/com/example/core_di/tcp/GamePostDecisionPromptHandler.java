package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.session.postgame.PostGameDecisionPrompt;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles GAME_POST_DECISION_PROMPT frames prompting users for rematch/leave choices.
 */
public final class GamePostDecisionPromptHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "PostDecisionPromptHdler";

    private final PostGameSessionStore postGameSessionStore;

    public GamePostDecisionPromptHandler(@NonNull PostGameSessionStore postGameSessionStore) {
        super(TAG, "GAME_POST_DECISION_PROMPT");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        long deadlineAt = root.optLong("deadlineAt", 0L);

        JSONArray optionsArray = root.optJSONArray("options");
        List<PostGameDecisionOption> options = new ArrayList<>();
        if (optionsArray != null) {
            for (int i = 0; i < optionsArray.length(); i++) {
                String label = optionsArray.optString(i, "");
                PostGameDecisionOption option = PostGameDecisionOption.fromLabel(label);
                if (option != PostGameDecisionOption.UNKNOWN) {
                    options.add(option);
                }
            }
        }

        String autoActionLabel = root.optString("autoAction", "");
        PostGameDecisionOption autoAction = PostGameDecisionOption.fromLabel(autoActionLabel);

        Log.i(TAG, "Post-game decision prompt → sessionId=" + sessionId
                + ", deadlineAt=" + deadlineAt
                + ", options=" + options);

        PostGameDecisionPrompt prompt = new PostGameDecisionPrompt(sessionId, deadlineAt, options, autoAction);
        postGameSessionStore.updatePrompt(prompt);
    }
}
