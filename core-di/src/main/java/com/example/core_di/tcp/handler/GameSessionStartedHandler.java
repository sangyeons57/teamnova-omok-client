package com.example.core_di.tcp.handler;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.core_di.tcp.processor.BoardPayloadProcessor;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        JSONArray ruleCodesJson = root.optJSONArray("ruleIds");
        List<String> activeRules = resolveRuleCodes(ruleCodesJson);
        if (activeRules.isEmpty()) {
            gameInfoStore.clearActiveRules();
        } else {
            gameInfoStore.updateActiveRules(activeRules);
        }

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
    }

    @NonNull
    private List<String> resolveRuleCodes(@Nullable JSONArray ruleCodesJson) {
        if (ruleCodesJson == null || ruleCodesJson.length() == 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>(ruleCodesJson.length());
        for (int i = 0; i < ruleCodesJson.length(); i++) {
            String rawCode = ruleCodesJson.optString(i, null);
            if (rawCode == null) {
                continue;
            }
            String normalized = rawCode.trim();
            if (normalized.isEmpty()) {
                continue;
            }
            result.add(normalized);
        }
        return result;
    }
}
