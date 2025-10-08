package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.protocol.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Handles GAME_SESSION_STARTED broadcasts containing board and initial turn state.
 */
public final class GameSessionStartedHandler implements ClientFrameHandler {

    private static final String TAG = "GameSessionStartedHdl";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;

    public GameSessionStartedHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, gameInfoStore.getBoardStore());
    }

    GameSessionStartedHandler(@NonNull GameInfoStore gameInfoStore,
                              @NonNull OmokBoardStore boardStore) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
    }

    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            Log.w(TAG, "Received null frame for GAME_SESSION_STARTED");
            return ClientDispatchResult.continueDispatch();
        }
        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            Log.w(TAG, "GAME_SESSION_STARTED payload missing");
            return ClientDispatchResult.continueDispatch();
        }

        String raw = new String(payload, StandardCharsets.UTF_8);
        Log.d(TAG, "GAME_SESSION_STARTED payload: " + raw);

        try {
            JSONObject root = new JSONObject(raw);
            String sessionId = root.optString("sessionId", "");
            long startedAt = root.optLong("startedAt", 0L);
            Log.i(TAG, "Game session started → sessionId=" + sessionId + ", startedAt=" + startedAt);

            JSONObject boardJson = root.optJSONObject("board");
            int width = 0;
            int height = 0;
            if (boardJson != null) {
                width = boardJson.optInt("width", 0);
                height = boardJson.optInt("height", 0);
            }
            if (width > 0 && height > 0) {
                boardStore.initializeBoard(width, height);
                Log.i(TAG, "Board initialized with size " + width + "x" + height);
            } else {
                Log.w(TAG, "Board dimensions missing or invalid in GAME_SESSION_STARTED payload");
            }

            JSONObject turnJson = root.optJSONObject("turn");
            TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse GAME_SESSION_STARTED payload", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error handling GAME_SESSION_STARTED frame", e);
        }

        return ClientDispatchResult.continueDispatch();
    }
}
