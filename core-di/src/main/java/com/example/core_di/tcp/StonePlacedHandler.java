package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles STONE_PLACED broadcasts to keep the local board in sync with the server.
 */
public final class StonePlacedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "StonePlacedHandler";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;

    public StonePlacedHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, gameInfoStore.getBoardStore());
    }

    StonePlacedHandler(@NonNull GameInfoStore gameInfoStore,
                       @NonNull OmokBoardStore boardStore) {
        super(TAG, "STONE_PLACED");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String placedBy = root.optString("placedBy", "");
        int x = root.optInt("x", -1);
        int y = root.optInt("y", -1);
        String stoneLabel = root.optString("stone", "");

        OmokStoneType stoneType = StoneTypeMapper.fromNetworkLabel(stoneLabel);
        if (stoneType == OmokStoneType.UNKNOWN && !stoneLabel.isEmpty()) {
            Log.w(TAG, "Unknown stone label '" + stoneLabel + "'. Treating as UNKNOWN.");
        }
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

        JSONObject boardJson = root.optJSONObject("board");
        if (boardJson == null && root.has("width") && root.has("height")) {
            boardJson = root;
        }
        if (boardJson != null) {
            BoardPayloadProcessor.applyBoardSnapshot(boardJson, boardStore, TAG);
        }

        JSONObject turnJson = root.optJSONObject("turn");
        TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
    }
}
