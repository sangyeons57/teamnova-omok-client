package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.protocol.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * Handles STONE_PLACED broadcasts to keep the local board in sync with the server.
 */
public final class StonePlacedHandler implements ClientFrameHandler {

    private static final String TAG = "StonePlacedHandler";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;

    public StonePlacedHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, gameInfoStore.getBoardStore());
    }

    StonePlacedHandler(@NonNull GameInfoStore gameInfoStore,
                       @NonNull OmokBoardStore boardStore) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
    }

    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            Log.w(TAG, "Received null frame for STONE_PLACED");
            return ClientDispatchResult.continueDispatch();
        }
        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            Log.w(TAG, "STONE_PLACED payload missing");
            return ClientDispatchResult.continueDispatch();
        }

        String raw = new String(payload, StandardCharsets.UTF_8);
        Log.d(TAG, "STONE_PLACED payload: " + raw);

        try {
            JSONObject root = new JSONObject(raw);
            String sessionId = root.optString("sessionId", "");
            String placedBy = root.optString("placedBy", "");
            int x = root.optInt("x", -1);
            int y = root.optInt("y", -1);
            String stoneLabel = root.optString("stone", "");

            OmokStoneType stoneType = parseStoneType(stoneLabel);
            Log.i(TAG, "Stone placed → sessionId=" + sessionId
                    + ", placedBy=" + placedBy
                    + ", stone=" + stoneType
                    + ", coord=(" + x + "," + y + ")");

            if (stoneType.isPlaced() && x >= 0 && y >= 0) {
                try {
                    boardStore.applyStone(new OmokStonePlacement(x, y, stoneType));
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Log.e(TAG, "Stone coordinate outside board bounds (" + x + "," + y + ")", e);
                }
            } else {
                Log.w(TAG, "Skipping board update due to invalid stone or coordinates.");
            }

            JSONObject turnJson = root.optJSONObject("turn");
            TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse STONE_PLACED payload", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error handling STONE_PLACED frame", e);
        }

        return ClientDispatchResult.continueDispatch();
    }

    @NonNull
    private OmokStoneType parseStoneType(@NonNull String stoneLabel) {
        if (stoneLabel.isEmpty()) {
            return OmokStoneType.UNKNOWN;
        }
        String normalized = stoneLabel.toUpperCase(Locale.US);
        try {
            return OmokStoneType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Unknown stone label '" + stoneLabel + "'. Treating as UNKNOWN.");
            return OmokStoneType.UNKNOWN;
        }
    }
}
