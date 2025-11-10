package com.example.core_di.tcp.handler;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.MatchState;
import com.example.application.session.postgame.PostGameSessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles GAME_SESSION_REMATCH_STARTED frames announcing a new session for rematch participants.
 */
public final class GameSessionRematchStartedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "GameRematchStartedHdl";

    private final PostGameSessionStore postGameSessionStore;
    private final GameInfoStore gameInfoStore;

    public GameSessionRematchStartedHandler(@NonNull PostGameSessionStore postGameSessionStore,
                                            @NonNull GameInfoStore gameInfoStore) {
        super(TAG, "GAME_SESSION_REMATCH_STARTED");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String rematchSessionId = root.optString("rematchSessionId", "");
        JSONArray participantsArray = root.optJSONArray("participants");
        List<String> participants = new ArrayList<>();
        if (participantsArray != null) {
            for (int i = 0; i < participantsArray.length(); i++) {
                String userId = participantsArray.optString(i, "");
                if (!userId.isEmpty()) {
                    participants.add(userId);
                }
            }
        }

        Log.i(TAG, "Rematch started → sessionId=" + sessionId
                + ", rematchSessionId=" + rematchSessionId
                + ", participants=" + participants.size());

        postGameSessionStore.markRematchStarted(sessionId, rematchSessionId, participants);

        gameInfoStore.clearTurnState();
        gameInfoStore.getBoardStore().clearBoard();
        gameInfoStore.updateMatchState(MatchState.MATCHING);
    }
}
