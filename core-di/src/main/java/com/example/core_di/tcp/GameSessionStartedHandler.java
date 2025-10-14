package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles GAME_SESSION_STARTED broadcasts containing board and initial turn state.
 */
public final class GameSessionStartedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "GameSessionStartedHdl";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;

    public GameSessionStartedHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, gameInfoStore.getBoardStore());
    }

    GameSessionStartedHandler(@NonNull GameInfoStore gameInfoStore,
                              @NonNull OmokBoardStore boardStore) {
        super(TAG, "GAME_SESSION_STARTED");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        long startedAt = root.optLong("startedAt", 0L);
        Log.i(TAG, "Game session started → sessionId=" + sessionId + ", startedAt=" + startedAt);

        JSONObject boardJson = root.optJSONObject("board");
        boolean boardApplied = BoardPayloadProcessor.applyBoardSnapshot(boardJson, boardStore, TAG);
        if (!boardApplied) {
            int width = boardJson != null ? boardJson.optInt("width", 0) : 0;
            int height = boardJson != null ? boardJson.optInt("height", 0) : 0;
            if (width > 0 && height > 0) {
                boardStore.initializeBoard(width, height);
                Log.i(TAG, "Board initialized with size " + width + "x" + height);
            } else {
                Log.w(TAG, "Board dimensions missing or invalid in GAME_SESSION_STARTED payload");
            }
        }

        JSONObject turnJson = root.optJSONObject("turn");
        TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
    }
}
